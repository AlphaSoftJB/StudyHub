package com.alphasoft.studyhub.identity.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response body for all auth API failures.
 *
 * Security note: messages are intentionally generic to avoid leaking
 * system internals or PII about existing accounts.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int        status,
    String     error,
    String     message,
    Instant    timestamp,
    List<String> fieldErrors   // non-null only on 400 validation failures
) {
    /** Factory for simple errors (no field-level detail). */
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now(), null);
    }

    /** Factory for validation errors with field-level messages. */
    public static ErrorResponse validation(List<String> fieldErrors) {
        return new ErrorResponse(
            400,
            "Bad Request",
            "Validation failed",
            Instant.now(),
            fieldErrors
        );
    }
}
