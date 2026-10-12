using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Controllers;

/// <summary>Diagnostic centers. Anyone can browse; only admins can change them.</summary>
[ApiController]
[Route("api/centers")]
public class CentersController(AppDbContext db) : ControllerBase
{
    [HttpGet]
    public async Task<IReadOnlyList<CenterDto>> GetAll([FromQuery] string? search, CancellationToken ct)
    {
        var q = db.DiagnosticCenters.AsNoTracking().Include(c => c.Tests).AsQueryable();
        if (!string.IsNullOrWhiteSpace(search))
        {
            var s = search.Trim().ToLower();
            q = q.Where(c => c.Name.ToLower().Contains(s) || c.Address.ToLower().Contains(s));
        }
        var centers = await q.OrderBy(c => c.Name).ToListAsync(ct);
        return centers.Select(c => c.ToDto()).ToList();
    }

    [HttpGet("{id:int}")]
    public async Task<CenterDetailDto> Get(int id, CancellationToken ct)
    {
        var center = await db.DiagnosticCenters.AsNoTracking().Include(c => c.Tests).FirstOrDefaultAsync(c => c.Id == id, ct)
                     ?? throw new NotFoundException("Diagnostic center not found.");
        return center.ToDetailDto();
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpPost]
    [ProducesResponseType<CenterDto>(StatusCodes.Status201Created)]
    public async Task<ActionResult<CenterDto>> Create(CenterRequest request, CancellationToken ct)
    {
        await EnsureUniqueName(request.Name, null, ct);
        var center = new DiagnosticCenter
        {
            Name = request.Name.Trim(),
            ContactNo = request.ContactNo.Trim(),
            Address = request.Address.Trim(),
            ContactEmail = request.ContactEmail.Trim(),
        };
        db.DiagnosticCenters.Add(center);
        await db.SaveChangesAsync(ct);
        return CreatedAtAction(nameof(Get), new { id = center.Id }, center.ToDto());
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpPut("{id:int}")]
    public async Task<CenterDto> Update(int id, CenterRequest request, CancellationToken ct)
    {
        var center = await db.DiagnosticCenters.Include(c => c.Tests).FirstOrDefaultAsync(c => c.Id == id, ct)
                     ?? throw new NotFoundException("Diagnostic center not found.");
        await EnsureUniqueName(request.Name, id, ct);

        center.Name = request.Name.Trim();
        center.ContactNo = request.ContactNo.Trim();
        center.Address = request.Address.Trim();
        center.ContactEmail = request.ContactEmail.Trim();
        await db.SaveChangesAsync(ct);
        return center.ToDto();
    }

    /// <summary>Deletes the center with its tests, appointments and results.</summary>
    [Authorize(Roles = Roles.Admin)]
    [HttpDelete("{id:int}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> Delete(int id, CancellationToken ct)
    {
        var center = await db.DiagnosticCenters.FindAsync([id], ct) ?? throw new NotFoundException("Diagnostic center not found.");
        db.DiagnosticCenters.Remove(center);
        await db.SaveChangesAsync(ct);
        return NoContent();
    }

    private async Task EnsureUniqueName(string name, int? exceptId, CancellationToken ct)
    {
        var n = name.Trim().ToLower();
        if (await db.DiagnosticCenters.AnyAsync(c => c.Name.ToLower() == n && c.Id != exceptId, ct))
            throw new ConflictException("A diagnostic center with this name already exists.");
    }
}
