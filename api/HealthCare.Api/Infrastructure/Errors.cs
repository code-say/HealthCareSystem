using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;

namespace HealthCare.Api.Infrastructure;

/// <summary>Base type for errors that map to a specific HTTP status.</summary>
public abstract class ApiException(int statusCode, string title, string message) : Exception(message)
{
    public int StatusCode { get; } = statusCode;
    public string Title { get; } = title;
}

public sealed class NotFoundException(string message) : ApiException(StatusCodes.Status404NotFound, "Not found", message);

public sealed class ConflictException(string message) : ApiException(StatusCodes.Status409Conflict, "Conflict", message);

public sealed class BadRequestException(string message) : ApiException(StatusCodes.Status400BadRequest, "Invalid request", message);

public sealed class ForbiddenException(string message) : ApiException(StatusCodes.Status403Forbidden, "Forbidden", message);

/// <summary>Turns exceptions into RFC 7807 problem details responses.</summary>
public sealed class ApiExceptionHandler(IProblemDetailsService problemDetails, ILogger<ApiExceptionHandler> logger) : IExceptionHandler
{
    public async ValueTask<bool> TryHandleAsync(HttpContext context, Exception exception, CancellationToken cancellationToken)
    {
        var (status, title, detail) = exception switch
        {
            ApiException api => (api.StatusCode, api.Title, api.Message),
            _ => (StatusCodes.Status500InternalServerError, "Server error", "Something went wrong. Please try again."),
        };

        if (status >= 500)
            logger.LogError(exception, "Unhandled exception for {Method} {Path}", context.Request.Method, context.Request.Path);

        context.Response.StatusCode = status;
        return await problemDetails.TryWriteAsync(new ProblemDetailsContext
        {
            HttpContext = context,
            Exception = exception,
            ProblemDetails = new ProblemDetails { Status = status, Title = title, Detail = detail },
        });
    }
}
