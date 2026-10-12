using HealthCare.Api.Entities;

namespace HealthCare.Api.Dtos;

public static class Mapping
{
    public static CenterDto ToDto(this DiagnosticCenter c) =>
        new(c.Id, c.Name, c.ContactNo, c.Address, c.ContactEmail, c.Tests.Count);

    public static CenterDetailDto ToDetailDto(this DiagnosticCenter c) =>
        new(c.Id, c.Name, c.ContactNo, c.Address, c.ContactEmail,
            c.Tests.OrderBy(t => t.TestName).Select(t => t.ToDto()).ToList());

    public static TestDto ToDto(this DiagnosticTest t) =>
        new(t.Id, t.TestName, t.TestPrice, t.NormalValue, t.Units, t.DiagnosticCenterId, t.DiagnosticCenter?.Name);

    public static PatientDto ToDto(this Patient p) =>
        new(p.Id, p.Name, p.PhoneNo, p.Age, p.Gender, p.User?.Email, p.Appointments.Count);

    public static TestResultDto ToDto(this TestResult r) =>
        new(r.Id, r.AppointmentId, r.DiagnosticTestId,
            r.DiagnosticTest?.TestName ?? string.Empty,
            r.TestReading,
            r.DiagnosticTest?.Units ?? string.Empty,
            r.DiagnosticTest?.NormalValue ?? string.Empty,
            r.Condition,
            r.RecordedAt,
            r.Appointment?.Patient?.Name);

    public static AppointmentDto ToDto(this Appointment a) =>
        new(a.Id,
            a.AppointmentDate,
            a.Status,
            a.PatientId,
            a.Patient?.Name ?? string.Empty,
            a.DiagnosticCenterId,
            a.DiagnosticCenter?.Name ?? string.Empty,
            a.Tests.Sum(t => t.TestPrice),
            a.Tests.OrderBy(t => t.TestName)
                .Select(t => new AppointmentTestDto(t.Id, t.TestName, t.TestPrice, t.NormalValue, t.Units)).ToList(),
            a.TestResults.OrderBy(r => r.DiagnosticTest?.TestName).Select(r => r.ToDto()).ToList());
}
