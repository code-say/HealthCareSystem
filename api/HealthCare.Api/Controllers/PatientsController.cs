using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Controllers;

/// <summary>Patient records. Admins manage everyone; patients see and edit their own profile.</summary>
[ApiController]
[Authorize]
[Route("api/patients")]
public class PatientsController(AppDbContext db) : ControllerBase
{
    // ----- the signed-in patient -----

    [Authorize(Roles = Roles.Patient)]
    [HttpGet("me")]
    public async Task<PatientDto> GetMe(CancellationToken ct) => (await Find(MyId(), ct)).ToDto();

    [Authorize(Roles = Roles.Patient)]
    [HttpPut("me")]
    public async Task<PatientDto> UpdateMe(PatientRequest request, CancellationToken ct) => await Apply(MyId(), request, ct);

    // ----- admin -----

    [Authorize(Roles = Roles.Admin)]
    [HttpGet]
    public async Task<IReadOnlyList<PatientDto>> GetAll([FromQuery] string? search, CancellationToken ct)
    {
        var q = db.Patients.AsNoTracking().Include(p => p.User).Include(p => p.Appointments).AsQueryable();
        if (!string.IsNullOrWhiteSpace(search))
        {
            var s = search.Trim().ToLower();
            q = q.Where(p => p.Name.ToLower().Contains(s) || p.PhoneNo.Contains(s) || (p.User != null && p.User.Email.Contains(s)));
        }
        var patients = await q.OrderBy(p => p.Name).ToListAsync(ct);
        return patients.Select(p => p.ToDto()).ToList();
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpGet("{id:int}")]
    public async Task<PatientDto> Get(int id, CancellationToken ct) => (await Find(id, ct)).ToDto();

    /// <summary>Register a walk-in patient (no login account).</summary>
    [Authorize(Roles = Roles.Admin)]
    [HttpPost]
    [ProducesResponseType<PatientDto>(StatusCodes.Status201Created)]
    public async Task<ActionResult<PatientDto>> Create(PatientRequest request, CancellationToken ct)
    {
        var patient = new Patient
        {
            Name = request.Name.Trim(),
            PhoneNo = request.PhoneNo.Trim(),
            Age = request.Age,
            Gender = request.Gender.Trim(),
        };
        db.Patients.Add(patient);
        await db.SaveChangesAsync(ct);
        return CreatedAtAction(nameof(Get), new { id = patient.Id }, patient.ToDto());
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpPut("{id:int}")]
    public Task<PatientDto> Update(int id, PatientRequest request, CancellationToken ct) => Apply(id, request, ct);

    /// <summary>Deletes the patient, their login and all their appointments.</summary>
    [Authorize(Roles = Roles.Admin)]
    [HttpDelete("{id:int}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> Delete(int id, CancellationToken ct)
    {
        var patient = await Find(id, ct);
        if (patient.User is not null) db.Users.Remove(patient.User);
        else db.Patients.Remove(patient);
        await db.SaveChangesAsync(ct);
        return NoContent();
    }

    private int MyId() => User.GetPatientId() ?? throw new ForbiddenException("This account has no patient profile.");

    private async Task<Patient> Find(int id, CancellationToken ct) =>
        await db.Patients.Include(p => p.User).Include(p => p.Appointments).FirstOrDefaultAsync(p => p.Id == id, ct)
        ?? throw new NotFoundException("Patient not found.");

    private async Task<PatientDto> Apply(int id, PatientRequest request, CancellationToken ct)
    {
        var patient = await Find(id, ct);
        patient.Name = request.Name.Trim();
        patient.PhoneNo = request.PhoneNo.Trim();
        patient.Age = request.Age;
        patient.Gender = request.Gender.Trim();
        await db.SaveChangesAsync(ct);
        return patient.ToDto();
    }
}
