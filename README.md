# auto-api-client-java

[![Maven Central](https://img.shields.io/maven-central/v/com.autoapi/auto-api-client)](https://central.sonatype.com/artifact/com.autoapi/auto-api-client)
[![Java](https://img.shields.io/badge/Java-11%2B-blue)](https://openjdk.org)
[![License](https://img.shields.io/github/license/autoapicom/auto-api-java)](LICENSE)

Java client for [auto-api.com](https://auto-api.com) — car listings API across multiple marketplaces.

One API to access car listings from 8 marketplaces: encar, mobile.de, autoscout24, che168, dongchedi, guazi, dubicars, dubizzle. Search offers, track price changes, and get listing data in a unified format.

## Installation

### Gradle

```groovy
implementation 'com.autoapi:auto-api-client:1.0.0'
```

### Maven

```xml
<dependency>
    <groupId>com.autoapi</groupId>
    <artifactId>auto-api-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

```java
import com.autoapi.client.AutoApiClient;
import com.autoapi.client.model.*;

AutoApiClient client = new AutoApiClient("your-api-key");
```

### Get filters

```java
Map<String, Object> filters = client.getFilters("encar");
```

### Search offers

```java
OffersResponse offers = client.getOffers("mobilede", new OffersParams()
        .page(1)
        .brand("BMW")
        .yearFrom(2020));

// Pagination
System.out.println(offers.getMeta().getPage());
System.out.println(offers.getMeta().getNextPage());
```

### Get single offer

```java
OffersResponse offer = client.getOffer("encar", "40427050");
```

### Track changes

```java
int changeId = client.getChangeId("encar", "2025-01-15");
ChangesResponse changes = client.getChanges("encar", changeId);

// Next batch
ChangesResponse nextBatch = client.getChanges("encar", changes.getMeta().getNextChangeId());
```

### Get offer by URL

```java
Map<String, Object> info = client.getOfferByUrl(
        "https://www.encar.com/dc/dc_cardetailview.do?carid=40427050");
```

### Decode offer data

Offer data varies between sources, so it's stored as Gson's `JsonElement`. Decode into `OfferData` or your own class:

```java
Gson gson = new Gson();
for (OfferItem item : offers.getResult()) {
    OfferData d = gson.fromJson(item.getData(), OfferData.class);
    System.out.printf("%s %s %s — $%s%n", d.getMark(), d.getModel(), d.getYear(), d.getPrice());
}
```

### Error handling

```java
import com.autoapi.client.exception.ApiException;
import com.autoapi.client.exception.AuthException;

try {
    OffersResponse offers = client.getOffers("encar", new OffersParams().page(1));
} catch (AuthException e) {
    // 401/403 — invalid API key
    System.out.println(e.getStatusCode() + ": " + e.getMessage());
} catch (ApiException e) {
    // Any other API error
    System.out.println(e.getStatusCode() + ": " + e.getMessage());
    System.out.println(e.getResponseBody());
}
```

## Supported sources

| Source | Platform | Region |
|--------|----------|--------|
| `encar` | [encar.com](https://www.encar.com) | South Korea |
| `mobilede` | [mobile.de](https://www.mobile.de) | Germany |
| `autoscout24` | [autoscout24.com](https://www.autoscout24.com) | Europe |
| `che168` | [che168.com](https://www.che168.com) | China |
| `dongchedi` | [dongchedi.com](https://www.dongchedi.com) | China |
| `guazi` | [guazi.com](https://www.guazi.com) | China |
| `dubicars` | [dubicars.com](https://www.dubicars.com) | UAE |
| `dubizzle` | [dubizzle.com](https://www.dubizzle.com) | UAE |

## Other languages

| Language | Package |
|----------|---------|
| PHP | [auto-api/client](https://github.com/autoapicom/auto-api-php) |
| TypeScript | [@auto-api/client](https://github.com/autoapicom/auto-api-node) |
| Python | [auto-api-client](https://github.com/autoapicom/auto-api-python) |
| Go | [auto-api-go](https://github.com/autoapicom/auto-api-go) |
| C# | [AutoApi.Client](https://github.com/autoapicom/auto-api-dotnet) |
| Ruby | [auto-api-client](https://github.com/autoapicom/auto-api-ruby) |
| Rust | [auto-api-client](https://github.com/autoapicom/auto-api-rust) |

## Documentation

[auto-api.com](https://auto-api.com)
