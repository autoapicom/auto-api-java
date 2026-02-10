# GitHub Copilot Instructions — auto-api-java

This is a Java client library for the auto-api.com API.

## Architecture

- `AutoApiClient` is the main class with 6 synchronous methods
- Gson for JSON serialization/deserialization
- `java.net.http.HttpClient` (built-in) for HTTP requests
- Fluent builder pattern for `OffersParams`
- `JsonElement` for raw offer data that varies between sources

## Exceptions

- `ApiException` extends `RuntimeException` — base exception for API errors
- `AuthException` extends `ApiException` — authentication errors (401/403)
- All exceptions are unchecked (no checked exceptions)

## Code Style

- Java 11+, only external dependency is Gson
- `@SerializedName` for snake_case JSON field mapping
- camelCase methods, PascalCase classes
- Javadoc on every public type and method, in English

## Language

All code, comments, and documentation must be in English.
