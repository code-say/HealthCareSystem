using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Services;

public interface IAppointmentService
{
    Task<IReadOnlyList<AppointmentDto>> ListAsync(int? patientId, AppointmentStatus? status, CancellationToken ct = default);
    Task<AppointmentDto> GetAsync(int id, int? restrictToPatientId, CancellationToken ct = default);
    Task<AppointmentDto> CreateAsync(int patientId, CreateAppointmentRequest request, CancellationToken ct = default);
    Task<AppointmentDto> UpdateStatusAsync(int id, AppointmentStatus status, CancellationToken ct = default);
    Task<AppointmentDto> CancelAsync(int id, int? restrictToPatientId, CancellationToken ct = default);
    Task DeleteAsync(int id, CancellationToken ct = default);
}

/// <summary>Business rules for booking and approving appointments.</summary>
public sealed class AppointmentService(AppDbContext db, TimeProvider clock) : IAppointmentService
{
    private IQueryable<Appointment> Query() => db.Appointments
        .Include(a => a.Patient)
        .Include(a => a.DiagnosticCenter)
        .Include(a => a.Tests)
        .Include(a => a.TestResults).ThenInclude(r => r.DiagnosticTest)
        .AsSplitQuery();

    public async Task<IReadOnlyList<AppointmentDto>> ListAsync(int? patientId, AppointmentStatus? status, CancellationToken ct = default)
    {
        var q = Query().AsNoTracking();
        if (patientId is not null) q = q.Where(a => a.PatientId == patientId);
        if (status is not null) q = q.Where(a => a.Status == status);

        var items = await q.OrderByDescending(a => a.AppointmentDate).ThenByDescending(a => a.Id).ToListAsync(ct);
        return items.Select(a => a.ToDto()).ToList();
    }

    public async Task<AppointmentDto> GetAsync(int id, int? restrictToPatientId, CancellationToken ct = default) =>
        (await Find(id, restrictToPatientId, ct)).ToDto();

    public async Task<AppointmentDto> CreateAsync(int patientId, CreateAppointmentRequest request, CancellationToken ct = default)
    {
        if (!await db.Patients.AnyAsync(p => p.Id == patientId, ct))
            throw new NotFoundException("Patient not found.");

        var center = await db.DiagnosticCenters.FirstOrDefaultAsync(c => c.Id == request.DiagnosticCenterId, ct)
                     ?? throw new NotFoundException("Diagnostic center not found.");

        var today = DateOnly.FromDateTime(clock.GetUtcNow().UtcDateTime);
        if (request.AppointmentDate < today)
            throw new BadRequestException("Appointment date can't be in the past.");
        if (request.AppointmentDate > today.AddMonths(6))
            throw new BadRequestException("Appointments can be booked up to 6 months ahead.");

        var testIds = request.TestIds.Distinct().ToList();
        var tests = await db.DiagnosticTests.Where(t => testIds.Contains(t.Id)).ToListAsync(ct);
        if (tests.Count != testIds.Count)
            throw new BadRequestException("One or more selected tests don't exist.");
        if (tests.Any(t => t.DiagnosticCenterId != center.Id))
            throw new BadRequestException("All tests must be offered by the chosen diagnostic center.");

        var appointment = new Appointment
        {
            PatientId = patientId,
            DiagnosticCenterId = center.Id,
            AppointmentDate = request.AppointmentDate,
            Status = AppointmentStatus.Pending,
            Tests = tests,
        };
        db.Appointments.Add(appointment);
        await db.SaveChangesAsync(ct);

        return await GetAsync(appointment.Id, null, ct);
    }

    public async Task<AppointmentDto> UpdateStatusAsync(int id, AppointmentStatus status, CancellationToken ct = default)
    {
        var a = await Find(id, null, ct);

        var allowed = (a.Status, status) switch
        {
            (AppointmentStatus.Pending, AppointmentStatus.Approved or AppointmentStatus.Rejected) => true,
            (AppointmentStatus.Rejected, AppointmentStatus.Approved) => true,
            (AppointmentStatus.Approved, AppointmentStatus.Rejected) when a.TestResults.Count == 0 => true,
            (_, AppointmentStatus.Cancelled) when a.TestResults.Count == 0 => true,
            _ => false,
        };
        if (!allowed)
            throw new BadRequestException($"Can't change an appointment from {a.Status} to {status}.");

        a.Status = status;
        await db.SaveChangesAsync(ct);
        return a.ToDto();
    }

    public async Task<AppointmentDto> CancelAsync(int id, int? restrictToPatientId, CancellationToken ct = default)
    {
        var a = await Find(id, restrictToPatientId, ct);
        if (a.Status is AppointmentStatus.Cancelled)
            return a.ToDto();
        if (a.TestResults.Count > 0)
            throw new BadRequestException("This appointment already has test results and can't be cancelled.");

        a.Status = AppointmentStatus.Cancelled;
        await db.SaveChangesAsync(ct);
        return a.ToDto();
    }

    public async Task DeleteAsync(int id, CancellationToken ct = default)
    {
        var a = await db.Appointments.FindAsync([id], ct) ?? throw new NotFoundException("Appointment not found.");
        db.Appointments.Remove(a);
        await db.SaveChangesAsync(ct);
    }

    private async Task<Appointment> Find(int id, int? restrictToPatientId, CancellationToken ct)
    {
        var a = await Query().FirstOrDefaultAsync(x => x.Id == id, ct);
        // Patients get a 404 (not 403) for other people's appointments so ids can't be probed
        if (a is null || (restrictToPatientId is not null && a.PatientId != restrictToPatientId))
            throw new NotFoundException("Appointment not found.");
        return a;
    }
}
