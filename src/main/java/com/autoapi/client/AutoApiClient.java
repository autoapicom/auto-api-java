package com.autoapi.client;

import com.autoapi.client.exception.ApiException;
import com.autoapi.client.exception.AuthException;
import com.autoapi.client.model.ChangesResponse;
import com.autoapi.client.model.OffersParams;
import com.autoapi.client.model.OffersResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client for the auto-api.com car listings API.
 */
public class AutoApiClient {

    private final String apiKey;
    private final String baseUrl;
    private final String apiVersion;
    private final HttpClient httpClient;
    private final Gson gson;

    /**
     * Creates a new client with the given API key.
     *
     * @param apiKey API key from auto-api.com
     */
    public AutoApiClient(String apiKey) {
        this(apiKey, "https://api1.auto-api.com", "v2");
    }

    /**
     * Creates a new client with custom settings.
     *
     * @param apiKey     API key from auto-api.com
     * @param baseUrl    base URL override
     * @param apiVersion API version (default: "v2")
     */
    public AutoApiClient(String apiKey, String baseUrl, String apiVersion) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.apiVersion = apiVersion;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.gson = new Gson();
    }

    /**
     * Returns available filters for a source (brands, models, body types, etc.)
     *
     * @param source source platform name
     * @return filters as a raw JSON map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getFilters(String source) {
        String body = get(String.format("api/%s/%s/filters", apiVersion, source), null);
        return gson.fromJson(body, Map.class);
    }

    /**
     * Returns a paginated list of offers with optional filters.
     *
     * @param source source platform name
     * @param params query parameters (page, brand, model, etc.)
     * @return offers response with result list and pagination meta
     */
    public OffersResponse getOffers(String source, OffersParams params) {
        Map<String, String> query = params != null ? params.toQueryParams() : new LinkedHashMap<>();
        String body = get(String.format("api/%s/%s/offers", apiVersion, source), query);
        return gson.fromJson(body, OffersResponse.class);
    }

    /**
     * Returns a single offer by inner_id.
     *
     * @param source  source platform name
     * @param innerId offer inner ID
     * @return offers response with single result
     */
    public OffersResponse getOffer(String source, String innerId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("inner_id", innerId);
        String body = get(String.format("api/%s/%s/offer", apiVersion, source), query);
        return gson.fromJson(body, OffersResponse.class);
    }

    /**
     * Returns a change_id for the given date.
     *
     * @param source source platform name
     * @param date   date in yyyy-mm-dd format
     * @return change_id integer
     */
    public int getChangeId(String source, String date) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("date", date);
        String body = get(String.format("api/%s/%s/change_id", apiVersion, source), query);
        JsonObject obj = gson.fromJson(body, JsonObject.class);
        return obj.get("change_id").getAsInt();
    }

    /**
     * Returns a changes feed (added/changed/removed) starting from change_id.
     *
     * @param source   source platform name
     * @param changeId change ID to start from
     * @return changes response with result list and pagination meta
     */
    public ChangesResponse getChanges(String source, int changeId) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("change_id", String.valueOf(changeId));
        String body = get(String.format("api/%s/%s/changes", apiVersion, source), query);
        return gson.fromJson(body, ChangesResponse.class);
    }

    /**
     * Returns offer data by its URL on the marketplace.
     * Uses POST /api/v1/offer/info with x-api-key header.
     *
     * @param url marketplace offer URL
     * @return offer data as a raw JSON map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOfferByUrl(String url) {
        JsonObject payload = new JsonObject();
        payload.addProperty("url", url);
        String body = post("api/v1/offer/info", payload);
        return gson.fromJson(body, Map.class);
    }

    private String get(String endpoint, Map<String, String> query) {
        if (query == null) {
            query = new LinkedHashMap<>();
        }
        query.put("api_key", apiKey);

        String queryString = query.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        String url = String.format("%s/%s?%s", baseUrl, endpoint, queryString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        return execute(request);
    }

    private String post(String endpoint, JsonElement data) {
        String url = String.format("%s/%s", baseUrl, endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();

        return execute(request);
    }

    private String execute(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new ApiException(0, "Network error: " + e.getMessage(), "");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Request interrupted", "");
        }

        String body = response.body();
        int statusCode = response.statusCode();

        if (statusCode < 200 || statusCode >= 300) {
            handleError(statusCode, body);
        }

        return body;
    }

    private void handleError(int statusCode, String body) {
        String message = String.format("API error: %d", statusCode);

        try {
            JsonObject parsed = gson.fromJson(body, JsonObject.class);
            if (parsed != null && parsed.has("message")) {
                message = parsed.get("message").getAsString();
            }
        } catch (Exception ignored) {
            // Use default message if response is not valid JSON
        }

        if (statusCode == 401 || statusCode == 403) {
            throw new AuthException(statusCode, message, body);
        }

        throw new ApiException(statusCode, message, body);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
