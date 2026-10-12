using System.ComponentModel.DataAnnotations;
using HealthCare.Api.Entities;

namespace HealthCare.Api.Dtos;

// ---------- Auth ----------

public record RegisterRequest(
    [Required, EmailAddress, MaxLength(200)] string Email,
    [Required, MinLength(8), MaxLength(100)] string Password,
    [Required, MaxLength(100)] string Name,
    [Required, RegularExpression(@"^[0-9+\- ]{7,20}$", ErrorMessage = "Enter a valid phone number.")] string PhoneNo,
    [Range(0, 120)] int Age,
    [Required, MaxLength(20)] string Gender);

public record LoginRequest(
    [Required, EmailAddress] string Email,
    [Required] string Password);

public record UserDto(int Id, string Email, string Role, int? PatientId, string? Name);

public record AuthResponse(string Token, DateTime ExpiresAt, UserDto User);

// ---------- Diagnostic centers ----------

public record CenterRequest(
    [Required, MaxLength(120)] string Name,
    [Required, RegularExpression(@"^[0-9+\- ]{7,20}$", ErrorMessage = "Enter a valid phone number.")] string ContactNo,
    [Required, MaxLength(250)] string Address,
    [Required, EmailAddress, MaxLength(200)] string ContactEmail);

public record CenterDto(int Id, string Name, string ContactNo, string Address, string ContactEmail, int TestCount);

public record CenterDetailDto(int Id, string Name, string ContactNo, string Address, string ContactEmail, IReadOnlyList<TestDto> Tests);

// ---------- Diagnostic tests ----------

public record TestRequest(
    [Required, MaxLength(120)] string TestName,
    [Range(0, 1_000_000)] decimal TestPrice,
    [MaxLength(60)] string? NormalValue,
    [MaxLength(30)] string? Units,
    [Range(1, int.MaxValue)] int DiagnosticCenterId);

public record TestDto(int Id, string TestName, decimal TestPrice, string NormalValue, string Units, int DiagnosticCenterId, string? CenterName);

// ---------- Patients ----------

public record PatientRequest(
    [Required, MaxLength(100)] string Name,
    [Required, RegularExpression(@"^[0-9+\- ]{7,20}$", ErrorMessage = "Enter a valid phone number.")] string PhoneNo,
    [Range(0, 120)] int Age,
    [Required, MaxLength(20)] string Gender);

public record PatientDto(int Id, string Name, string PhoneNo, int Age, string Gender, string? Email, int AppointmentCount);

// ---------- Appointments ----------

public record CreateAppointmentRequest(
    [Range(1, int.MaxValue)] int DiagnosticCenterId,
    DateOnly AppointmentDate,
    [Required, MinLength(1, ErrorMessage = "Choose at least one test.")] List<int> TestIds,
    int? PatientId = null); // admins can book for any patient; patients always book for themselves

public record UpdateStatusRequest([Required] AppointmentStatus Status);

public record AppointmentTestDto(int Id, string TestName, decimal TestPrice, string NormalValue, string Units);

public record AppointmentDto(
    int Id,
    DateOnly AppointmentDate,
    AppointmentStatus Status,
    int PatientId,
    string PatientName,
    int DiagnosticCenterId,
    string CenterName,
    decimal TotalPrice,
    IReadOnlyList<AppointmentTestDto> Tests,
    IReadOnlyList<TestResultDto> Results);

// ---------- Test results ----------

public record TestResultRequest(
    [Range(1, int.MaxValue)] int AppointmentId,
    [Range(1, int.MaxValue)] int DiagnosticTestId,
    double TestReading,
    [Required, MaxLength(40)] string Condition);

public record UpdateTestResultRequest(double TestReading, [Required, MaxLength(40)] string Condition);

public record TestResultDto(
    int Id,
    int AppointmentId,
    int DiagnosticTestId,
    string TestName,
    double TestReading,
    string Units,
    string NormalValue,
    string Condition,
    DateTime RecordedAt,
    string? PatientName);

// ---------- Dashboard ----------

public record StatsDto(int Patients, int Centers, int Tests, int PendingAppointments, int ApprovedAppointments, int Results);
