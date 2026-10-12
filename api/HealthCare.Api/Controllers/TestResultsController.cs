using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Controllers;

/// <summary>Test results recorded against approved appointments.</summary>
[ApiController]
[Authorize]
[Route("api/test-results")]
public class TestResultsController(AppDbContext db) : ControllerBase
{
    private IQueryable<TestResult> Query() => db.TestResults
        .Include(r => r.DiagnosticTest)
        .Include(r => r.Appointment).ThenInclude(a => a!.Patient);

    /// <summary>Admins see all results; patients see their own.</summary>
    [HttpGet]
    public async Task<IReadOnlyList<TestResultDto>> GetAll(CancellationToken ct)
    {
        var q = Query().AsNoTracking();
        if (!User.IsAdmin())
        {
            var patientId = User.GetPatientId() ?? throw new ForbiddenException("This account has no patient profile.");
            q = q.Where(r => r.Appointment!.PatientId == patientId);
        }
        var results = await q.OrderByDescending(r => r.RecordedAt).ToListAsync(ct);
        return results.Select(r => r.ToDto()).ToList();
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpPost]
    [ProducesResponseType<TestResultDto>(StatusCodes.Status201Created)]
    public async Task<ActionResult<TestResultDto>> Create(TestResultRequest request, CancellationToken ct)
    {
        var appointment = await db.Appointments.Include(a => a.Tests).Include(a => a.TestResults)
                              .FirstOrDefaultAsync(a => a.Id == request.AppointmentId, ct)
                          ?? throw new NotFoundException("Appointment not found.");

        if (appointment.Status != AppointmentStatus.Approved)
            throw new BadRequestException("Results can only be added to approved appointments.");
        if (appointment.Tests.All(t => t.Id != request.DiagnosticTestId))
            throw new BadRequestException("That test isn't part of this appointment.");
        if (appointment.TestResults.Any(r => r.DiagnosticTestId == request.DiagnosticTestId))
            throw new ConflictException("A result for this test has already been recorded. Edit it instead.");

        var result = new TestResult
        {
            AppointmentId = appointment.Id,
            DiagnosticTestId = request.DiagnosticTestId,
            TestReading = request.TestReading,
            Condition = request.Condition.Trim(),
        };
        db.TestResults.Add(result);
        await db.SaveChangesAsync(ct);

        var dto = (await Query().AsNoTracking().FirstAsync(r => r.Id == result.Id, ct)).ToDto();
        return Created($"/api/test-results/{result.Id}", dto);
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpPut("{id:int}")]
    public async Task<TestResultDto> Update(int id, UpdateTestResultRequest request, CancellationToken ct)
    {
        var result = await Query().FirstOrDefaultAsync(r => r.Id == id, ct) ?? throw new NotFoundException("Test result not found.");
        result.TestReading = request.TestReading;
        result.Condition = request.Condition.Trim();
        result.RecordedAt = DateTime.UtcNow;
        await db.SaveChangesAsync(ct);
        return result.ToDto();
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpDelete("{id:int}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> Delete(int id, CancellationToken ct)
    {
        var result = await db.TestResults.FindAsync([id], ct) ?? throw new NotFoundException("Test result not found.");
        db.TestResults.Remove(result);
        await db.SaveChangesAsync(ct);
        return NoContent();
    }
}
