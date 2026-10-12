using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using HealthCare.Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace HealthCare.Api.Controllers;

/// <summary>Booking, approving and cancelling appointments.</summary>
[ApiController]
[Authorize]
[Route("api/appointments")]
public class AppointmentsController(IAppointmentService appointments) : ControllerBase
{
    /// <summary>Admins see every appointment; patients see their own.</summary>
    [HttpGet]
    public Task<IReadOnlyList<AppointmentDto>> GetAll([FromQuery] AppointmentStatus? status, CancellationToken ct) =>
        appointments.ListAsync(User.IsAdmin() ? null : MyPatientId(), status, ct);

    [HttpGet("{id:int}")]
    public Task<AppointmentDto> Get(int id, CancellationToken ct) =>
        appointments.GetAsync(id, User.IsAdmin() ? null : MyPatientId(), ct);

    /// <summary>Book an appointment. It starts as Pending until an admin approves it.</summary>
    [HttpPost]
    [ProducesResponseType<AppointmentDto>(StatusCodes.Status201Created)]
    public async Task<ActionResult<AppointmentDto>> Create(CreateAppointmentRequest request, CancellationToken ct)
    {
        int patientId;
        if (User.IsAdmin())
            patientId = request.PatientId ?? throw new BadRequestException("Choose the patient to book for.");
        else
            patientId = MyPatientId();

        var created = await appointments.CreateAsync(patientId, request, ct);
        return CreatedAtAction(nameof(Get), new { id = created.Id }, created);
    }

    /// <summary>Approve or reject an appointment.</summary>
    [Authorize(Roles = Roles.Admin)]
    [HttpPut("{id:int}/status")]
    public Task<AppointmentDto> UpdateStatus(int id, UpdateStatusRequest request, CancellationToken ct) =>
        appointments.UpdateStatusAsync(id, request.Status, ct);

    /// <summary>Cancel an appointment (patients can cancel their own).</summary>
    [HttpPost("{id:int}/cancel")]
    public Task<AppointmentDto> Cancel(int id, CancellationToken ct) =>
        appointments.CancelAsync(id, User.IsAdmin() ? null : MyPatientId(), ct);

    [Authorize(Roles = Roles.Admin)]
    [HttpDelete("{id:int}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> Delete(int id, CancellationToken ct)
    {
        await appointments.DeleteAsync(id, ct);
        return NoContent();
    }

    private int MyPatientId() => User.GetPatientId() ?? throw new ForbiddenException("This account has no patient profile.");
}
