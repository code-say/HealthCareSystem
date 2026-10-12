namespace HealthCare.Api.Entities;

public static class Roles
{
    public const string Admin = "Admin";
    public const string Patient = "Patient";
}

public enum AppointmentStatus
{
    Pending,
    Approved,
    Rejected,
    Cancelled,
}

public class User
{
    public int Id { get; set; }
    public required string Email { get; set; }
    public string PasswordHash { get; set; } = string.Empty;
    public required string Role { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public Patient? Patient { get; set; }
}

public class Patient
{
    public int Id { get; set; }
    public required string Name { get; set; }
    public string PhoneNo { get; set; } = string.Empty;
    public int Age { get; set; }
    public string Gender { get; set; } = string.Empty;

    /// <summary>Login account, if the patient registered themselves. Admins can also add walk-in patients without one.</summary>
    public int? UserId { get; set; }
    public User? User { get; set; }

    public List<Appointment> Appointments { get; set; } = [];
}

public class DiagnosticCenter
{
    public int Id { get; set; }
    public required string Name { get; set; }
    public string ContactNo { get; set; } = string.Empty;
    public string Address { get; set; } = string.Empty;
    public string ContactEmail { get; set; } = string.Empty;

    public List<DiagnosticTest> Tests { get; set; } = [];
    public List<Appointment> Appointments { get; set; } = [];
}

public class DiagnosticTest
{
    public int Id { get; set; }
    public required string TestName { get; set; }
    public decimal TestPrice { get; set; }
    public string NormalValue { get; set; } = string.Empty;
    public string Units { get; set; } = string.Empty;

    public int DiagnosticCenterId { get; set; }
    public DiagnosticCenter? DiagnosticCenter { get; set; }

    public List<Appointment> Appointments { get; set; } = [];
}

public class Appointment
{
    public int Id { get; set; }
    public DateOnly AppointmentDate { get; set; }
    public AppointmentStatus Status { get; set; } = AppointmentStatus.Pending;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public int PatientId { get; set; }
    public Patient? Patient { get; set; }

    public int DiagnosticCenterId { get; set; }
    public DiagnosticCenter? DiagnosticCenter { get; set; }

    public List<DiagnosticTest> Tests { get; set; } = [];
    public List<TestResult> TestResults { get; set; } = [];
}

public class TestResult
{
    public int Id { get; set; }
    public double TestReading { get; set; }
    public string Condition { get; set; } = string.Empty;
    public DateTime RecordedAt { get; set; } = DateTime.UtcNow;

    public int AppointmentId { get; set; }
    public Appointment? Appointment { get; set; }

    public int DiagnosticTestId { get; set; }
    public DiagnosticTest? DiagnosticTest { get; set; }
}
