using HealthCare.Api.Entities;
using Microsoft.EntityFrameworkCore;

namespace HealthCare.Api.Data;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<User> Users => Set<User>();
    public DbSet<Patient> Patients => Set<Patient>();
    public DbSet<DiagnosticCenter> DiagnosticCenters => Set<DiagnosticCenter>();
    public DbSet<DiagnosticTest> DiagnosticTests => Set<DiagnosticTest>();
    public DbSet<Appointment> Appointments => Set<Appointment>();
    public DbSet<TestResult> TestResults => Set<TestResult>();

    protected override void OnModelCreating(ModelBuilder b)
    {
        b.Entity<User>(e =>
        {
            e.HasIndex(u => u.Email).IsUnique();
            e.Property(u => u.Email).HasMaxLength(200);
            e.Property(u => u.Role).HasMaxLength(20);
            e.HasOne(u => u.Patient).WithOne(p => p.User).HasForeignKey<Patient>(p => p.UserId).OnDelete(DeleteBehavior.Cascade);
        });

        b.Entity<Patient>(e =>
        {
            e.Property(p => p.Name).HasMaxLength(100);
            e.Property(p => p.PhoneNo).HasMaxLength(20);
            e.Property(p => p.Gender).HasMaxLength(20);
        });

        b.Entity<DiagnosticCenter>(e =>
        {
            e.HasIndex(c => c.Name).IsUnique();
            e.Property(c => c.Name).HasMaxLength(120);
        });

        b.Entity<DiagnosticTest>(e =>
        {
            e.Property(t => t.TestName).HasMaxLength(120);
            // SQLite has no decimal type; store as double-precision for correct ordering/comparison
            e.Property(t => t.TestPrice).HasConversion<double>();
            e.HasOne(t => t.DiagnosticCenter).WithMany(c => c.Tests).HasForeignKey(t => t.DiagnosticCenterId).OnDelete(DeleteBehavior.Cascade);
        });

        b.Entity<Appointment>(e =>
        {
            e.Property(a => a.Status).HasConversion<string>().HasMaxLength(20);
            e.HasOne(a => a.Patient).WithMany(p => p.Appointments).HasForeignKey(a => a.PatientId).OnDelete(DeleteBehavior.Cascade);
            e.HasOne(a => a.DiagnosticCenter).WithMany(c => c.Appointments).HasForeignKey(a => a.DiagnosticCenterId).OnDelete(DeleteBehavior.Cascade);
            e.HasMany(a => a.Tests).WithMany(t => t.Appointments).UsingEntity("AppointmentTests");
        });

        b.Entity<TestResult>(e =>
        {
            e.HasIndex(r => new { r.AppointmentId, r.DiagnosticTestId }).IsUnique();
            e.HasOne(r => r.Appointment).WithMany(a => a.TestResults).HasForeignKey(r => r.AppointmentId).OnDelete(DeleteBehavior.Cascade);
            e.HasOne(r => r.DiagnosticTest).WithMany().HasForeignKey(r => r.DiagnosticTestId).OnDelete(DeleteBehavior.Cascade);
        });
    }
}
