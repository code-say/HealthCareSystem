using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Controllers;

/// <summary>Diagnostic tests offered by each center.</summary>
[ApiController]
[Route("api/tests")]
public class TestsController(AppDbContext db) : ControllerBase
{
    [HttpGet]
    public async Task<IReadOnlyList<TestDto>> GetAll([FromQuery] int? centerId, CancellationToken ct)
    {
        var q = db.DiagnosticTests.AsNoTracking().Include(t => t.DiagnosticCenter).AsQueryable();
        if (centerId is not null) q = q.Where(t => t.DiagnosticCenterId == centerId);
        var tests = await q.OrderBy(t => t.DiagnosticCenter!.Name).ThenBy(t => t.TestName).ToListAsync(ct);
        return tests.Select(t => t.ToDto()).ToList();
    }

    [HttpGet("{id:int}")]
    public async Task<TestDto> Get(int id, CancellationToken ct) => (await Find(id, ct)).ToDto();

    [Authorize(Roles = Roles.Admin)]
    [HttpPost]
    [ProducesResponseType<TestDto>(StatusCodes.Status201Created)]
    public async Task<ActionResult<TestDto>> Create(TestRequest request, CancellationToken ct)
    {
        await EnsureValid(request, null, ct);
        var test = new DiagnosticTest
        {
            TestName = request.TestName.Trim(),
            TestPrice = request.TestPrice,
            NormalValue = request.NormalValue?.Trim() ?? string.Empty,
            Units = request.Units?.Trim() ?? string.Empty,
            DiagnosticCenterId = request.DiagnosticCenterId,
        };
        db.DiagnosticTests.Add(test);
        await db.SaveChangesAsync(ct);
        return CreatedAtAction(nameof(Get), new { id = test.Id }, (await Find(test.Id, ct)).ToDto());
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpPut("{id:int}")]
    public async Task<TestDto> Update(int id, TestRequest request, CancellationToken ct)
    {
        var test = await Find(id, ct);
        await EnsureValid(request, id, ct);

        test.TestName = request.TestName.Trim();
        test.TestPrice = request.TestPrice;
        test.NormalValue = request.NormalValue?.Trim() ?? string.Empty;
        test.Units = request.Units?.Trim() ?? string.Empty;
        test.DiagnosticCenterId = request.DiagnosticCenterId;
        await db.SaveChangesAsync(ct);
        return (await Find(id, ct)).ToDto();
    }

    [Authorize(Roles = Roles.Admin)]
    [HttpDelete("{id:int}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> Delete(int id, CancellationToken ct)
    {
        var test = await Find(id, ct);
        db.DiagnosticTests.Remove(test);
        await db.SaveChangesAsync(ct);
        return NoContent();
    }

    private async Task<DiagnosticTest> Find(int id, CancellationToken ct) =>
        await db.DiagnosticTests.Include(t => t.DiagnosticCenter).FirstOrDefaultAsync(t => t.Id == id, ct)
        ?? throw new NotFoundException("Diagnostic test not found.");

    private async Task EnsureValid(TestRequest request, int? exceptId, CancellationToken ct)
    {
        if (!await db.DiagnosticCenters.AnyAsync(c => c.Id == request.DiagnosticCenterId, ct))
            throw new BadRequestException("Diagnostic center not found.");

        var name = request.TestName.Trim().ToLower();
        if (await db.DiagnosticTests.AnyAsync(t => t.DiagnosticCenterId == request.DiagnosticCenterId
                                                   && t.TestName.ToLower() == name && t.Id != exceptId, ct))
            throw new ConflictException("This center already offers a test with that name.");
    }
}
