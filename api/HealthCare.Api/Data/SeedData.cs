using HealthCare.Api.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Data;

/// <summary>Demo data so a fresh database is immediately usable.</summary>
public static class SeedData
{
    public const string AdminEmail = "admin@healthcare.demo";
    public const string AdminPassword = "Admin@12345";
    public const string PatientEmail = "patient@healthcare.demo";
    public const string PatientPassword = "Patient@12345";

    public static async Task InitializeAsync(AppDbContext db, IPasswordHasher<User> hasher, TimeProvider clock, CancellationToken ct = default)
    {
        await db.Database.EnsureCreatedAsync(ct);
        if (await db.Users.AnyAsync(ct))
            return;

        var admin = new User { Email = AdminEmail, Role = Roles.Admin };
        admin.PasswordHash = hasher.HashPassword(admin, AdminPassword);

        var patientUser = new User { Email = PatientEmail, Role = Roles.Patient };
        patientUser.PasswordHash = hasher.HashPassword(patientUser, PatientPassword);
        patientUser.Patient = new Patient { Name = "Ananya Sharma", PhoneNo = "+91 98300 12345", Age = 29, Gender = "Female" };

        var walkIn = new Patient { Name = "Rahul Verma", PhoneNo = "+91 99220 67890", Age = 41, Gender = "Male" };

        var centers = new[]
        {
            Center("CareFirst Diagnostics", "+91 33 4000 1100", "12 Park Street, Kolkata, West Bengal", "contact@carefirst.demo"),
            Center("Sahyadri Path Labs", "+91 20 6700 2200", "48 FC Road, Shivajinagar, Pune, Maharashtra", "hello@sahyadrilabs.demo"),
            Center("Lakeview Health Centre", "+91 80 4100 3300", "7 Indiranagar 100 Ft Road, Bengaluru, Karnataka", "info@lakeview.demo"),
        };

        foreach (var c in centers)
        {
            c.Tests.AddRange(
            [
                Test("Complete Blood Count (CBC)", 350, "4.5-11.0", "x10^9/L"),
                Test("Fasting Blood Sugar", 120, "70-100", "mg/dL"),
                Test("Lipid Profile (Total Cholesterol)", 650, "< 200", "mg/dL"),
                Test("Thyroid (TSH)", 400, "0.4-4.0", "mIU/L"),
                Test("Vitamin D (25-OH)", 1200, "30-100", "ng/mL"),
                Test("Haemoglobin", 150, "12.0-15.5", "g/dL"),
            ]);
        }

        db.Users.AddRange(admin, patientUser);
        db.Patients.Add(walkIn);
        db.DiagnosticCenters.AddRange(centers);
        await db.SaveChangesAsync(ct);

        // A few appointments in different states, so both dashboards have something to show
        var today = DateOnly.FromDateTime(clock.GetUtcNow().UtcDateTime);
        var kolkata = centers[0];
        var pune = centers[1];
        var ananya = patientUser.Patient!;

        var past = new Appointment
        {
            Patient = ananya, DiagnosticCenter = kolkata, AppointmentDate = today.AddDays(-14), Status = AppointmentStatus.Approved,
            Tests = [kolkata.Tests[0], kolkata.Tests[1], kolkata.Tests[3]],
        };
        var upcoming = new Appointment
        {
            Patient = ananya, DiagnosticCenter = pune, AppointmentDate = today.AddDays(5), Status = AppointmentStatus.Pending,
            Tests = [pune.Tests[2], pune.Tests[4]],
        };
        var walkInVisit = new Appointment
        {
            Patient = walkIn, DiagnosticCenter = kolkata, AppointmentDate = today.AddDays(2), Status = AppointmentStatus.Pending,
            Tests = [kolkata.Tests[5]],
        };
        db.Appointments.AddRange(past, upcoming, walkInVisit);
        await db.SaveChangesAsync(ct);

        db.TestResults.AddRange(
            new TestResult { Appointment = past, DiagnosticTest = kolkata.Tests[0], TestReading = 7.2, Condition = "Normal" },
            new TestResult { Appointment = past, DiagnosticTest = kolkata.Tests[1], TestReading = 112, Condition = "High" },
            new TestResult { Appointment = past, DiagnosticTest = kolkata.Tests[3], TestReading = 2.1, Condition = "Normal" });
        await db.SaveChangesAsync(ct);
    }

    private static DiagnosticCenter Center(string name, string phone, string address, string email) =>
        new() { Name = name, ContactNo = phone, Address = address, ContactEmail = email };

    private static DiagnosticTest Test(string name, decimal price, string normal, string units) =>
        new() { TestName = name, TestPrice = price, NormalValue = normal, Units = units };
}
