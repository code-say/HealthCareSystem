# HealthCare System · ASP.NET Core + React

Book diagnostic tests online: patients choose a diagnostic center, pick tests and a date, and track their appointment from approval to results. Admins approve or reject appointments, record test results, and manage centers, tests and patients.

[![CI](https://github.com/code-say/HealthCareSystem/actions/workflows/ci.yml/badge.svg)](https://github.com/code-say/HealthCareSystem/actions/workflows/ci.yml)

- **Live app:** https://health-care-system-green.vercel.app
- **API reference:** https://healthcare-api-tsts.onrender.com/scalar/v1

> The API runs on Render's free tier, which sleeps when idle, so the first request can take up to a minute. Demo data resets when the server restarts.

**Demo accounts**

| Role | Email | Password |
|---|---|---|
| Patient | `patient@healthcare.demo` | `Patient@12345` |
| Admin | `admin@healthcare.demo` | `Admin@12345` |

## Tech stack

| Layer | Technology |
|---|---|
| API | ASP.NET Core Web API (.NET 10), C#, controllers + service layer |
| Data | Entity Framework Core, SQLite (swap the provider for SQL Server in `Program.cs`) |
| Auth | JWT bearer tokens, role-based authorization (Admin / Patient), ASP.NET Core Identity password hashing |
| Docs | OpenAPI 3.1 + Scalar API reference |
| Client | React 18, React Router 6, Vite, hand-written CSS |
| Tests | xUnit integration tests (WebApplicationFactory) + Playwright end-to-end flow |
| Delivery | Docker, GitHub Actions CI, Render (API), Vercel (client) |

## Features

**Patients**: sign up / sign in, browse centers and test prices, book one or more tests at a center for a date, see live total, cancel, view status and results with normal ranges, edit profile.

**Admins**: dashboard counts, approve / reject appointments, record test results (only on approved appointments, one per test), create / edit / delete centers, tests and patients (including walk-in patients without a login).

**API design**
- RESTful routes under `/api` (`/auth`, `/centers`, `/tests`, `/patients`, `/appointments`, `/test-results`, `/stats`)
- Validation with DataAnnotations; every error is an RFC 7807 problem details response
- Business rules in `AppointmentService`: no past dates, tests must belong to the chosen center, allowed status transitions, no cancelling once results exist
- Patients can only read their own records (other ids return 404, so they can't be probed)
- Demo data seeded on first start

## Project structure

```
api/
  HealthCare.Api/           ASP.NET Core Web API
    Controllers/            Auth, Centers, Tests, Patients, Appointments, TestResults, Stats
    Services/               AuthService, TokenService (JWT), AppointmentService
    Entities/  Dtos/  Data/ EF Core model, request/response records, DbContext + seed
    Infrastructure/         problem-details exception handler, claims helpers, OpenAPI bearer scheme
  HealthCare.Api.Tests/     xUnit integration tests
  Dockerfile
client/                     React app (Vite)
  src/pages/                Home, Centers, SignIn, SignUp, PatientDashboard, AdminDashboard
  e2e/flow.mjs              Playwright end-to-end flow (run in CI)
render.yaml                 Render blueprint for the API
.github/workflows/ci.yml    build, tests, E2E, Docker image
```

## Run locally

Requires the .NET 10 SDK and Node.js 22.

```bash
# API on http://localhost:5080 (API reference at /scalar/v1)
dotnet run --project api/HealthCare.Api

# Client on http://localhost:5173
cd client
cp .env.example .env.local
npm install
npm run dev
```

Run the tests with `dotnet test`.

## History

This started in January 2022 as a Capgemini sprint project built with Spring Boot, Maven and MySQL ([original code in the git history](https://github.com/code-say/HealthCareSystem/tree/8dd44ec7b82888dae9243563a9de2f0e010f5e38)), with a separate React frontend in [HealthCareSystem-React](https://github.com/code-say/HealthCareSystem-React). In 2026 it was rewritten as a single ASP.NET Core + React project with real authentication, validation, tests and deployment.
