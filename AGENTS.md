# auto-api Java Client

Java client for [auto-api.com](https://auto-api.com) — car listings API across 8 marketplaces.

## Quick Start

```groovy
implementation 'com.autoapi:auto-api-client:1.0.0'
```

```java
import com.autoapi.client.AutoApiClient;
import com.autoapi.client.model.*;

AutoApiClient client = new AutoApiClient("your-api-key");
OffersResponse offers = client.getOffers("encar", new OffersParams().page(1));
```

## Build & Test

```bash
gradle build
gradle test
gradle javadoc
cd examples && gradle run
```

## Key Files

- `src/main/java/com/autoapi/client/AutoApiClient.java` — Client class, 6 methods, HTTP helpers
- `src/main/java/com/autoapi/client/model/OffersParams.java` — Fluent builder for query parameters
- `src/main/java/com/autoapi/client/model/OffersResponse.java` — Offers response POJO
- `src/main/java/com/autoapi/client/model/ChangesResponse.java` — Changes response POJO
- `src/main/java/com/autoapi/client/model/Meta.java` — Offers pagination
- `src/main/java/com/autoapi/client/model/ChangesMeta.java` — Changes pagination
- `src/main/java/com/autoapi/client/model/OfferItem.java` — Offer element with raw JsonElement data
- `src/main/java/com/autoapi/client/model/ChangeItem.java` — Change element with raw JsonElement data
- `src/main/java/com/autoapi/client/model/OfferData.java` — Common offer fields for convenience
- `src/main/java/com/autoapi/client/exception/ApiException.java` — Base API exception
- `src/main/java/com/autoapi/client/exception/AuthException.java` — Auth exception (401/403)
- `build.gradle` — Gradle build file with dependencies and publishing
- `settings.gradle` — Project name

## Conventions

- Java 11+, only dependency is Gson for JSON parsing
- java.net.http.HttpClient — built-in since Java 11
- Synchronous API — simpler, like Go client
- @SerializedName for snake_case JSON to camelCase Java mapping
- JsonElement for raw JSON data — structure varies between sources
- Fluent builder pattern for OffersParams
- Unchecked exceptions (RuntimeException) — modern Java convention
- camelCase methods/fields, PascalCase classes — Java convention
- Javadoc comments on every public type and method, in English

## API Methods

| Method | Params | Returns |
|--------|--------|---------|
| `getFilters(source)` | source name | `Map<String, Object>` |
| `getOffers(source, params)` | source + OffersParams | `OffersResponse` |
| `getOffer(source, innerId)` | source + inner_id | `OffersResponse` |
| `getChangeId(source, date)` | source + yyyy-mm-dd | `int` |
| `getChanges(source, changeId)` | source + change_id | `ChangesResponse` |
| `getOfferByUrl(url)` | marketplace URL | `Map<String, Object>` |
