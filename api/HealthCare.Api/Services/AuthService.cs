using HealthCare.Api.Data;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;
using HealthCare.Api.Infrastructure;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Services;

public interface IAuthService
{
    Task<AuthResponse> RegisterPatientAsync(RegisterRequest request, CancellationToken ct = default);
    Task<AuthResponse> LoginAsync(LoginRequest request, CancellationToken ct = default);
    Task<UserDto> GetUserAsync(int userId, CancellationToken ct = default);
}

public sealed class AuthService(AppDbContext db, IPasswordHasher<User> hasher, ITokenService tokens) : IAuthService
{
    public async Task<AuthResponse> RegisterPatientAsync(RegisterRequest request, CancellationToken ct = default)
    {
        var email = request.Email.Trim().ToLowerInvariant();
        if (await db.Users.AnyAsync(u => u.Email == email, ct))
            throw new ConflictException("An account with this email already exists.");

        var user = new User { Email = email, Role = Roles.Patient };
        user.PasswordHash = hasher.HashPassword(user, request.Password);
        user.Patient = new Patient
        {
            Name = request.Name.Trim(),
            PhoneNo = request.PhoneNo.Trim(),
            Age = request.Age,
            Gender = request.Gender.Trim(),
        };

        db.Users.Add(user);
        await db.SaveChangesAsync(ct);
        return Issue(user);
    }

    public async Task<AuthResponse> LoginAsync(LoginRequest request, CancellationToken ct = default)
    {
        var email = request.Email.Trim().ToLowerInvariant();
        var user = await db.Users.Include(u => u.Patient).FirstOrDefaultAsync(u => u.Email == email, ct);

        // Same message for unknown email and wrong password, so accounts can't be probed
        const string invalid = "Incorrect email or password.";
        if (user is null)
            throw new BadRequestException(invalid);

        var result = hasher.VerifyHashedPassword(user, user.PasswordHash, request.Password);
        if (result == PasswordVerificationResult.Failed)
            throw new BadRequestException(invalid);

        if (result == PasswordVerificationResult.SuccessRehashNeeded)
        {
            user.PasswordHash = hasher.HashPassword(user, request.Password);
            await db.SaveChangesAsync(ct);
        }

        return Issue(user);
    }

    public async Task<UserDto> GetUserAsync(int userId, CancellationToken ct = default)
    {
        var user = await db.Users.AsNoTracking().Include(u => u.Patient).FirstOrDefaultAsync(u => u.Id == userId, ct)
                   ?? throw new NotFoundException("User not found.");
        return ToDto(user);
    }

    private AuthResponse Issue(User user)
    {
        var (token, expires) = tokens.CreateToken(user);
        return new AuthResponse(token, expires, ToDto(user));
    }

    private static UserDto ToDto(User u) => new(u.Id, u.Email, u.Role, u.Patient?.Id, u.Patient?.Name ?? "Administrator");
}
