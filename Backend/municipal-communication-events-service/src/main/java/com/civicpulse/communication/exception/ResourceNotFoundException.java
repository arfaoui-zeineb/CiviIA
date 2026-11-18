package com.civicpulse.communication.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s with id '%s' not found", resourceName, id), HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}

