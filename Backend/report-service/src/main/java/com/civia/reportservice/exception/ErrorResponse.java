package com.civia.reportservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
    public ErrorResponse(int status, String error, String message, String path, List<String> details) {
        this(Instant.now(), status, error, message, path, details != null ? details : Collections.emptyList());
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path, Collections.emptyList());
    }
}
