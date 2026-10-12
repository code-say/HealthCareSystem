using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Controllers;

/// <summary>Counts for the admin dashboard.</summary>
[ApiController]
[Authorize(Roles = Roles.Admin)]
[Route("api/stats")]
public class StatsController(AppDbContext db) : ControllerBase
{
    [HttpGet]
    public async Task<StatsDto> Get(CancellationToken ct) => new(
        await db.Patients.CountAsync(ct),
        await db.DiagnosticCenters.CountAsync(ct),
        await db.DiagnosticTests.CountAsync(ct),
        await db.Appointments.CountAsync(a => a.Status == AppointmentStatus.Pending, ct),
        await db.Appointments.CountAsync(a => a.Status == AppointmentStatus.Approved, ct),
        await db.TestResults.CountAsync(ct));
}
