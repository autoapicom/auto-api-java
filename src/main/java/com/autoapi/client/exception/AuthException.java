package com.autoapi.client.exception;

/**
 * Exception for authentication errors (HTTP 401/403).
 */
public class AuthException extends ApiException {

    public AuthException(int statusCode, String message, String responseBody) {
        super(statusCode, message, responseBody);
    }
}
