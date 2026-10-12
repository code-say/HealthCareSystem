using System.Net;
using System.Net.Http.Json;
using HealthCare.Api.Dtos;
using HealthCare.Api.Entities;

namespace HealthCare.Api.Tests;

public class ApiTests(ApiFactory factory) : IClassFixture<ApiFactory>
{
    private static DateOnly Today => DateOnly.FromDateTime(DateTime.UtcNow);

    [Fact]
    public async Task Health_and_api_reference_are_up()
    {
        var client = factory.CreateClient();
        Assert.Equal(HttpStatusCode.OK, (await client.GetAsync("/health")).StatusCode);
        Assert.Equal(HttpStatusCode.OK, (await client.GetAsync("/openapi/v1.json")).StatusCode);
    }

    [Fact]
    public async Task Anyone_can_browse_centers_and_tests()
    {
        var client = factory.CreateClient();
        var centers = await client.GetFromJsonAsync<List<CenterDto>>("/api/centers", Json.Options);
        Assert.NotNull(centers);
        Assert.True(centers!.Count >= 3);

        var detail = await client.GetFromJsonAsync<CenterDetailDto>($"/api/centers/{centers[0].Id}", Json.Options);
        Assert.NotEmpty(detail!.Tests);
    }

    [Fact]
    public async Task Wrong_password_is_rejected_with_problem_details()
    {
        var res = await factory.CreateClient().PostAsJsonAsync("/api/auth/login", new LoginRequest(Data.SeedData.AdminEmail, "nope"));
        Assert.Equal(HttpStatusCode.BadRequest, res.StatusCode);
        Assert.Equal("application/problem+json", res.Content.Headers.ContentType?.MediaType);
    }

    [Fact]
    public async Task Admin_endpoints_need_admin_role()
    {
        var anon = factory.CreateClient();
        Assert.Equal(HttpStatusCode.Unauthorized, (await anon.GetAsync("/api/patients")).StatusCode);

        var patient = await factory.PatientAsync();
        Assert.Equal(HttpStatusCode.Forbidden, (await patient.GetAsync("/api/patients")).StatusCode);
        Assert.Equal(HttpStatusCode.Forbidden,
            (await patient.PostAsJsonAsync("/api/centers", new CenterRequest("X", "1234567", "Addr", "a@b.com"))).StatusCode);

        var admin = await factory.AdminAsync();
        Assert.Equal(HttpStatusCode.OK, (await admin.GetAsync("/api/patients")).StatusCode);
        Assert.Equal(HttpStatusCode.OK, (await admin.GetAsync("/api/stats")).StatusCode);
    }

    [Fact]
    public async Task Register_then_login_and_see_own_profile()
    {
        var client = factory.CreateClient();
        var email = $"new-{Guid.NewGuid():N}@example.com";
        var res = await client.PostAsJsonAsync("/api/auth/register",
            new RegisterRequest(email, "Secret#2026", "Test Patient", "+91 90000 00000", 33, "Male"));
        Assert.Equal(HttpStatusCode.Created, res.StatusCode);

        var dup = await client.PostAsJsonAsync("/api/auth/register",
            new RegisterRequest(email, "Secret#2026", "Test Patient", "+91 90000 00000", 33, "Male"));
        Assert.Equal(HttpStatusCode.Conflict, dup.StatusCode);

        var me = await factory.ClientAsAsync(email, "Secret#2026");
        var profile = await me.GetFromJsonAsync<PatientDto>("/api/patients/me", Json.Options);
        Assert.Equal("Test Patient", profile!.Name);
        Assert.Equal(email, profile.Email);
    }

    [Fact]
    public async Task Invalid_registration_is_a_validation_error()
    {
        var res = await factory.CreateClient().PostAsJsonAsync("/api/auth/register",
            new RegisterRequest("not-an-email", "short", "", "abc", 200, ""));
        Assert.Equal(HttpStatusCode.BadRequest, res.StatusCode);
    }

    [Fact]
    public async Task Full_booking_flow_book_approve_record_result_and_view()
    {
        var patient = await factory.PatientAsync();
        var admin = await factory.AdminAsync();

        var center = (await patient.GetFromJsonAsync<List<CenterDto>>("/api/centers", Json.Options))![1];
        var tests = (await patient.GetFromJsonAsync<List<TestDto>>($"/api/tests?centerId={center.Id}", Json.Options))!;

        // Patient books two tests
        var book = await patient.PostAsJsonAsync("/api/appointments",
            new CreateAppointmentRequest(center.Id, Today.AddDays(3), [tests[0].Id, tests[1].Id]), Json.Options);
        Assert.Equal(HttpStatusCode.Created, book.StatusCode);
        var appt = (await book.Content.ReadFromJsonAsync<AppointmentDto>(Json.Options))!;
        Assert.Equal(AppointmentStatus.Pending, appt.Status);
        Assert.Equal(tests[0].TestPrice + tests[1].TestPrice, appt.TotalPrice);

        // Results can't be added before approval
        var early = await admin.PostAsJsonAsync("/api/test-results", new TestResultRequest(appt.Id, tests[0].Id, 5.5, "Normal"));
        Assert.Equal(HttpStatusCode.BadRequest, early.StatusCode);

        // Admin approves and records a result
        var approve = await admin.PutAsJsonAsync($"/api/appointments/{appt.Id}/status",
            new UpdateStatusRequest(AppointmentStatus.Approved), Json.Options);
        Assert.Equal(HttpStatusCode.OK, approve.StatusCode);

        var result = await admin.PostAsJsonAsync("/api/test-results", new TestResultRequest(appt.Id, tests[0].Id, 5.5, "Normal"));
        Assert.Equal(HttpStatusCode.Created, result.StatusCode);

        var again = await admin.PostAsJsonAsync("/api/test-results", new TestResultRequest(appt.Id, tests[0].Id, 6, "Normal"));
        Assert.Equal(HttpStatusCode.Conflict, again.StatusCode);

        // Patient sees the approved appointment with its result
        var mine = await patient.GetFromJsonAsync<AppointmentDto>($"/api/appointments/{appt.Id}", Json.Options);
        Assert.Equal(AppointmentStatus.Approved, mine!.Status);
        Assert.Single(mine.Results);
        Assert.Equal(5.5, mine.Results[0].TestReading);

        // ...and can no longer cancel it, because it has results
        var cancel = await patient.PostAsync($"/api/appointments/{appt.Id}/cancel", null);
        Assert.Equal(HttpStatusCode.BadRequest, cancel.StatusCode);
    }

    [Fact]
    public async Task Booking_rules_are_enforced()
    {
        var patient = await factory.PatientAsync();
        var centers = (await patient.GetFromJsonAsync<List<CenterDto>>("/api/centers", Json.Options))!;
        var otherCenterTest = (await patient.GetFromJsonAsync<List<TestDto>>($"/api/tests?centerId={centers[1].Id}", Json.Options))![0];
        var ownTest = (await patient.GetFromJsonAsync<List<TestDto>>($"/api/tests?centerId={centers[0].Id}", Json.Options))![0];

        var past = await patient.PostAsJsonAsync("/api/appointments",
            new CreateAppointmentRequest(centers[0].Id, Today.AddDays(-1), [ownTest.Id]));
        Assert.Equal(HttpStatusCode.BadRequest, past.StatusCode);

        var wrongCenter = await patient.PostAsJsonAsync("/api/appointments",
            new CreateAppointmentRequest(centers[0].Id, Today.AddDays(1), [otherCenterTest.Id]));
        Assert.Equal(HttpStatusCode.BadRequest, wrongCenter.StatusCode);

        var noTests = await patient.PostAsJsonAsync("/api/appointments",
            new CreateAppointmentRequest(centers[0].Id, Today.AddDays(1), []));
        Assert.Equal(HttpStatusCode.BadRequest, noTests.StatusCode);
    }

    [Fact]
    public async Task Patients_cannot_see_other_patients_appointments()
    {
        var admin = await factory.AdminAsync();
        var all = (await admin.GetFromJsonAsync<List<AppointmentDto>>("/api/appointments", Json.Options))!;
        var someoneElse = all.First(a => a.PatientName != "Ananya Sharma");

        var patient = await factory.PatientAsync();
        Assert.Equal(HttpStatusCode.NotFound, (await patient.GetAsync($"/api/appointments/{someoneElse.Id}")).StatusCode);

        var mine = (await patient.GetFromJsonAsync<List<AppointmentDto>>("/api/appointments", Json.Options))!;
        Assert.All(mine, a => Assert.Equal("Ananya Sharma", a.PatientName));
    }

    [Fact]
    public async Task Admin_can_manage_centers_and_tests()
    {
        var admin = await factory.AdminAsync();
        var name = $"Test Center {Guid.NewGuid():N}"[..24];

        var created = await admin.PostAsJsonAsync("/api/centers",
            new CenterRequest(name, "+91 11 2222 3333", "1 Test Road, Delhi", "test@center.demo"));
        Assert.Equal(HttpStatusCode.Created, created.StatusCode);
        var center = (await created.Content.ReadFromJsonAsync<CenterDto>(Json.Options))!;

        var dup = await admin.PostAsJsonAsync("/api/centers",
            new CenterRequest(name, "+91 11 2222 3333", "1 Test Road, Delhi", "test@center.demo"));
        Assert.Equal(HttpStatusCode.Conflict, dup.StatusCode);

        var test = await admin.PostAsJsonAsync("/api/tests", new TestRequest("Iron Studies", 900, "60-170", "mcg/dL", center.Id));
        Assert.Equal(HttpStatusCode.Created, test.StatusCode);

        var update = await admin.PutAsJsonAsync($"/api/centers/{center.Id}",
            new CenterRequest(name, "+91 11 9999 0000", "2 New Road, Delhi", "new@center.demo"));
        Assert.Equal(HttpStatusCode.OK, update.StatusCode);

        Assert.Equal(HttpStatusCode.NoContent, (await admin.DeleteAsync($"/api/centers/{center.Id}")).StatusCode);
        Assert.Equal(HttpStatusCode.NotFound, (await admin.GetAsync($"/api/centers/{center.Id}")).StatusCode);
    }
}
