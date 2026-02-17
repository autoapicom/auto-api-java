package com.autoapi.client;

import com.autoapi.client.exception.ApiException;
import com.autoapi.client.exception.AuthException;
import com.autoapi.client.model.OffersParams;
import com.autoapi.client.model.OffersResponse;
import com.autoapi.client.model.ChangesResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AutoApiClientTest {

    private HttpServer server;
    private String baseUrl;

    // Captured request details
    private String lastMethod;
    private String lastPath;
    private String lastQuery;
    private String lastBody;
    private Map<String, String> lastHeaders;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private AutoApiClient createClient() {
        return new AutoApiClient("test-api-key", baseUrl, "v2");
    }

    private void mockResponse(String path, int statusCode, String body) {
        server.createContext(path, exchange -> {
            captureRequest(exchange);
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
    }

    private void captureRequest(HttpExchange exchange) throws IOException {
        lastMethod = exchange.getRequestMethod();
        lastPath = exchange.getRequestURI().getPath();
        lastQuery = exchange.getRequestURI().getRawQuery();
        lastHeaders = new LinkedHashMap<>();
        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!values.isEmpty()) {
                lastHeaders.put(key.toLowerCase(), values.get(0));
            }
        });
        lastBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            params.put(key, value);
        }
        return params;
    }

    // ── getFilters ───────────────────────────────────────────────────────

    @Test
    void getFilters_returnsFilters() {
        String json = "{\"brands\":[\"BMW\",\"Audi\"],\"body_types\":[\"sedan\"]}";
        mockResponse("/api/v2/encar/filters", 200, json);

        AutoApiClient client = createClient();
        Map<String, Object> filters = client.getFilters("encar");

        assertNotNull(filters);
        assertNotNull(filters.get("brands"));
        assertNotNull(filters.get("body_types"));
    }

    @Test
    void getFilters_sendsCorrectRequest() {
        mockResponse("/api/v2/mobilede/filters", 200, "{}");

        createClient().getFilters("mobilede");

        assertEquals("GET", lastMethod);
        assertEquals("/api/v2/mobilede/filters", lastPath);
        Map<String, String> params = parseQuery(lastQuery);
        assertEquals("test-api-key", params.get("api_key"));
    }

    // ── getOffers ────────────────────────────────────────────────────────

    @Test
    void getOffers_returnsOffersWithMeta() {
        String json = "{\"result\":[{\"id\":1,\"inner_id\":\"123\",\"data\":{\"mark\":\"BMW\"}}],"
                + "\"meta\":{\"page\":1,\"next_page\":2,\"limit\":20}}";
        mockResponse("/api/v2/encar/offers", 200, json);

        AutoApiClient client = createClient();
        OffersResponse resp = client.getOffers("encar", new OffersParams().page(1));

        assertNotNull(resp.getResult());
        assertEquals(1, resp.getResult().size());
        assertEquals("123", resp.getResult().get(0).getInnerId());
        assertEquals(1, resp.getMeta().getPage());
        assertEquals(2, resp.getMeta().getNextPage());
        assertEquals(20, resp.getMeta().getLimit());
    }

    @Test
    void getOffers_sendsFilterParams() {
        mockResponse("/api/v2/autoscout24/offers", 200, "{\"result\":[],\"meta\":{\"page\":1,\"next_page\":0,\"limit\":20}}");

        createClient().getOffers("autoscout24", new OffersParams()
                .page(2)
                .brand("BMW")
                .yearFrom(2020)
                .priceTo(50000));

        assertEquals("GET", lastMethod);
        assertEquals("/api/v2/autoscout24/offers", lastPath);
        Map<String, String> params = parseQuery(lastQuery);
        assertEquals("2", params.get("page"));
        assertEquals("BMW", params.get("brand"));
        assertEquals("2020", params.get("year_from"));
        assertEquals("50000", params.get("price_to"));
        assertEquals("test-api-key", params.get("api_key"));
    }

    @Test
    void getOffers_worksWithNullParams() {
        mockResponse("/api/v2/encar/offers", 200, "{\"result\":[],\"meta\":{\"page\":0,\"next_page\":0,\"limit\":20}}");

        OffersResponse resp = createClient().getOffers("encar", null);

        assertNotNull(resp.getResult());
        assertEquals("GET", lastMethod);
    }

    // ── getOffer ─────────────────────────────────────────────────────────

    @Test
    void getOffer_returnsSingleOffer() {
        String json = "{\"result\":[{\"id\":1,\"inner_id\":\"40427050\",\"data\":{\"mark\":\"Hyundai\"}}],"
                + "\"meta\":{\"page\":1,\"next_page\":0,\"limit\":1}}";
        mockResponse("/api/v2/encar/offer", 200, json);

        OffersResponse resp = createClient().getOffer("encar", "40427050");

        assertEquals(1, resp.getResult().size());
        assertEquals("40427050", resp.getResult().get(0).getInnerId());
    }

    @Test
    void getOffer_sendsCorrectRequest() {
        mockResponse("/api/v2/mobilede/offer", 200, "{\"result\":[],\"meta\":{\"page\":1,\"next_page\":0,\"limit\":1}}");

        createClient().getOffer("mobilede", "ABC-123");

        assertEquals("GET", lastMethod);
        assertEquals("/api/v2/mobilede/offer", lastPath);
        Map<String, String> params = parseQuery(lastQuery);
        assertEquals("ABC-123", params.get("inner_id"));
        assertEquals("test-api-key", params.get("api_key"));
    }

    // ── getChangeId ──────────────────────────────────────────────────────

    @Test
    void getChangeId_returnsId() {
        mockResponse("/api/v2/encar/change_id", 200, "{\"change_id\":12345}");

        int changeId = createClient().getChangeId("encar", "2025-01-15");

        assertEquals(12345, changeId);
    }

    @Test
    void getChangeId_returnsZero() {
        mockResponse("/api/v2/encar/change_id", 200, "{\"change_id\":0}");

        int changeId = createClient().getChangeId("encar", "2020-01-01");

        assertEquals(0, changeId);
    }

    @Test
    void getChangeId_sendsDateParam() {
        mockResponse("/api/v2/che168/change_id", 200, "{\"change_id\":1}");

        createClient().getChangeId("che168", "2025-01-15");

        assertEquals("GET", lastMethod);
        assertEquals("/api/v2/che168/change_id", lastPath);
        Map<String, String> params = parseQuery(lastQuery);
        assertEquals("2025-01-15", params.get("date"));
        assertEquals("test-api-key", params.get("api_key"));
    }

    // ── getChanges ───────────────────────────────────────────────────────

    @Test
    void getChanges_returnsChanges() {
        String json = "{\"result\":[{\"id\":1,\"inner_id\":\"100\",\"change_type\":\"added\",\"data\":{}},"
                + "{\"id\":2,\"inner_id\":\"101\",\"change_type\":\"removed\",\"data\":{}}],"
                + "\"meta\":{\"cur_change_id\":100,\"next_change_id\":200,\"limit\":50}}";
        mockResponse("/api/v2/encar/changes", 200, json);

        ChangesResponse resp = createClient().getChanges("encar", 100);

        assertEquals(2, resp.getResult().size());
        assertEquals("added", resp.getResult().get(0).getChangeType());
        assertEquals("removed", resp.getResult().get(1).getChangeType());
        assertEquals(100, resp.getMeta().getCurChangeId());
        assertEquals(200, resp.getMeta().getNextChangeId());
    }

    @Test
    void getChanges_sendsCorrectRequest() {
        mockResponse("/api/v2/guazi/changes", 200, "{\"result\":[],\"meta\":{\"cur_change_id\":500,\"next_change_id\":600,\"limit\":50}}");

        createClient().getChanges("guazi", 500);

        assertEquals("GET", lastMethod);
        assertEquals("/api/v2/guazi/changes", lastPath);
        Map<String, String> params = parseQuery(lastQuery);
        assertEquals("500", params.get("change_id"));
        assertEquals("test-api-key", params.get("api_key"));
    }

    // ── getOfferByUrl ────────────────────────────────────────────────────

    @Test
    void getOfferByUrl_returnsData() {
        String json = "{\"url\":\"https://encar.com/dc/dc_cardetailview.do?carid=40427050\",\"mark\":\"Hyundai\"}";
        mockResponse("/api/v1/offer/info", 200, json);

        Map<String, Object> result = createClient().getOfferByUrl(
                "https://encar.com/dc/dc_cardetailview.do?carid=40427050");

        assertNotNull(result);
        assertEquals("Hyundai", result.get("mark"));
    }

    @Test
    void getOfferByUrl_sendsPostWithApiKeyHeader() {
        mockResponse("/api/v1/offer/info", 200, "{\"ok\":true}");

        createClient().getOfferByUrl("https://example.com/offer/123");

        assertEquals("POST", lastMethod);
        assertEquals("/api/v1/offer/info", lastPath);
        assertEquals("test-api-key", lastHeaders.get("x-api-key"));
        assertEquals("application/json", lastHeaders.get("content-type"));
        assertTrue(lastBody.contains("\"url\""));
        assertTrue(lastBody.contains("https://example.com/offer/123"));
        // Should NOT have api_key in query string for POST
        assertNull(lastQuery);
    }

    // ── Configuration ────────────────────────────────────────────────────

    @Test
    void customApiVersion() {
        mockResponse("/api/v3/encar/filters", 200, "{\"brands\":[]}");

        AutoApiClient client = new AutoApiClient("key", baseUrl, "v3");
        client.getFilters("encar");

        assertEquals("/api/v3/encar/filters", lastPath);
    }

    @Test
    void trailingSlashStripped() {
        mockResponse("/api/v2/encar/filters", 200, "{}");

        AutoApiClient client = new AutoApiClient("key", baseUrl + "///", "v2");
        client.getFilters("encar");

        assertEquals("/api/v2/encar/filters", lastPath);
    }

    // ── Error handling ───────────────────────────────────────────────────

    @Test
    void error500_throwsApiException() {
        mockResponse("/api/v2/encar/filters", 500, "{\"message\":\"Internal Server Error\"}");

        ApiException ex = assertThrows(ApiException.class, () ->
                createClient().getFilters("encar"));

        assertEquals(500, ex.getStatusCode());
        assertEquals("Internal Server Error", ex.getMessage());
        assertTrue(ex.getResponseBody().contains("Internal Server Error"));
    }

    @Test
    void error401_throwsAuthException() {
        mockResponse("/api/v2/encar/filters", 401, "{\"message\":\"Invalid API key\"}");

        AuthException ex = assertThrows(AuthException.class, () ->
                createClient().getFilters("encar"));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Invalid API key", ex.getMessage());
    }

    @Test
    void error403_throwsAuthException() {
        mockResponse("/api/v2/encar/filters", 403, "{\"message\":\"Forbidden\"}");

        AuthException ex = assertThrows(AuthException.class, () ->
                createClient().getFilters("encar"));

        assertEquals(403, ex.getStatusCode());
    }

    @Test
    void error404_throwsApiExceptionNotAuth() {
        mockResponse("/api/v2/encar/filters", 404, "{\"message\":\"Not found\"}");

        ApiException ex = assertThrows(ApiException.class, () ->
                createClient().getFilters("encar"));

        assertEquals(404, ex.getStatusCode());
        assertFalse(ex instanceof AuthException);
    }

    @Test
    void authException_isApiException() {
        mockResponse("/api/v2/encar/filters", 401, "{\"message\":\"Unauthorized\"}");

        assertThrows(ApiException.class, () ->
                createClient().getFilters("encar"));
    }

    @Test
    void errorWithInvalidJson_usesFallbackMessage() {
        mockResponse("/api/v2/encar/filters", 502, "Bad Gateway");

        ApiException ex = assertThrows(ApiException.class, () ->
                createClient().getFilters("encar"));

        assertEquals(502, ex.getStatusCode());
        assertEquals("API error: 502", ex.getMessage());
        assertEquals("Bad Gateway", ex.getResponseBody());
    }

    @Test
    void errorWithNoMessageField_usesFallbackMessage() {
        mockResponse("/api/v2/encar/filters", 500, "{\"error\":\"something\"}");

        ApiException ex = assertThrows(ApiException.class, () ->
                createClient().getFilters("encar"));

        assertEquals(500, ex.getStatusCode());
        assertEquals("API error: 500", ex.getMessage());
    }
}
