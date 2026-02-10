package com.autoapi.client.exception;

/**
 * Base exception for all auto-api.com API errors.
 * Extends RuntimeException (unchecked) — modern Java convention.
 */
public class ApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public ApiException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * HTTP status code returned by the API.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Raw response body from the API.
     */
    public String getResponseBody() {
        return responseBody;
    }
}
