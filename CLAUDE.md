# Claude Instructions — auto-api-java

## Language

All code comments, documentation, and README files must be written in **English**.

## Commands

- Build: `gradle build`
- Generate docs: `gradle javadoc`
- Run example: `cd examples && gradle run`

## Key Files

- `src/main/java/com/autoapi/client/AutoApiClient.java` — main client class (6 methods)
- `src/main/java/com/autoapi/client/model/` — data models:
  - `OffersParams.java` — query parameters (fluent builder)
  - `OffersResponse.java`, `ChangesResponse.java` — response wrappers
  - `Meta.java` — pagination metadata
  - `OfferItem.java`, `ChangeItem.java` — list items
  - `OfferData.java` — raw offer data container
- `src/main/java/com/autoapi/client/exception/` — exceptions:
  - `ApiException.java` — base API exception
  - `AuthException.java` — authentication errors (401/403)
- `build.gradle` — project configuration

## Code Style

- Java 11+
- Only dependency: Gson (for JSON serialization)
- `java.net.http.HttpClient` (built-in) for HTTP requests
- Synchronous API — no CompletableFuture or reactive
- `@SerializedName` for snake_case JSON field mapping
- Fluent builder pattern for `OffersParams`
- Unchecked exceptions (extend `RuntimeException`)
- `JsonElement` for raw offer data that varies between sources
- camelCase for methods, PascalCase for classes
- Javadoc on every public type and method, in English

## Conventions

- Never add more dependencies beyond Gson
- Never use checked exceptions
- Never use Lombok
- Keep it simple — no unnecessary abstractions
