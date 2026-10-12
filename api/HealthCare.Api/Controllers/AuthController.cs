using HealthCare.Api.Dtos;
using HealthCare.Api.Infrastructure;
using HealthCare.Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace HealthCare.Api.Controllers;

/// <summary>Sign up, sign in and the current user.</summary>
[ApiController]
[Route("api/auth")]
public class AuthController(IAuthService auth) : ControllerBase
{
    /// <summary>Create a patient account and sign in.</summary>
    [HttpPost("register")]
    [ProducesResponseType<AuthResponse>(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult<AuthResponse>> Register(RegisterRequest request, CancellationToken ct)
    {
        var result = await auth.RegisterPatientAsync(request, ct);
        return CreatedAtAction(nameof(Me), null, result);
    }

    /// <summary>Exchange email and password for a JWT.</summary>
    [HttpPost("login")]
    [ProducesResponseType<AuthResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public Task<AuthResponse> Login(LoginRequest request, CancellationToken ct) => auth.LoginAsync(request, ct);

    /// <summary>The signed-in user.</summary>
    [Authorize]
    [HttpGet("me")]
    public Task<UserDto> Me(CancellationToken ct) => auth.GetUserAsync(User.GetUserId(), ct);
}
