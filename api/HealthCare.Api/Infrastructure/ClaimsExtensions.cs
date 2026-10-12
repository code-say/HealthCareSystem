using System.Security.Claims;
using HealthCare.Api.Entities;

namespace HealthCare.Api.Infrastructure;

public static class ClaimsExtensions
{
    public const string PatientIdClaim = "patient_id";

    public static int GetUserId(this ClaimsPrincipal user) =>
        int.TryParse(user.FindFirstValue(ClaimTypes.NameIdentifier), out var id)
            ? id
            : throw new ForbiddenException("Missing user id.");

    public static int? GetPatientId(this ClaimsPrincipal user) =>
        int.TryParse(user.FindFirstValue(PatientIdClaim), out var id) ? id : null;

    public static bool IsAdmin(this ClaimsPrincipal user) => user.IsInRole(Roles.Admin);
}
