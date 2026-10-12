# Build
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /src
COPY HealthCare.Api/HealthCare.Api.csproj HealthCare.Api/
RUN dotnet restore HealthCare.Api/HealthCare.Api.csproj
COPY HealthCare.Api/ HealthCare.Api/
RUN dotnet publish HealthCare.Api/HealthCare.Api.csproj -c Release -o /app --no-restore

# Run
FROM mcr.microsoft.com/dotnet/aspnet:10.0
WORKDIR /app
COPY --from=build /app .
# SQLite file lives in /app/data (writable by the non-root app user)
RUN mkdir -p /app/data && chown -R $APP_UID /app/data
USER $APP_UID
# FORWARDEDHEADERS_ENABLED: trust X-Forwarded-Proto/For from the hosting proxy (Render terminates
# HTTPS), so the app knows requests are https (correct OpenAPI server URL, redirects, logs)
ENV ASPNETCORE_HTTP_PORTS=8080 \
    ASPNETCORE_FORWARDEDHEADERS_ENABLED=true \
    ConnectionStrings__Default="Data Source=/app/data/healthcare.db"
EXPOSE 8080
ENTRYPOINT ["dotnet", "HealthCare.Api.dll"]
