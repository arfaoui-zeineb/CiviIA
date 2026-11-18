package com.civicpulse.communication.integration.impl;

import com.civicpulse.communication.integration.DepartmentClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class DepartmentClientImpl implements DepartmentClient {

    @Value("${services.department.base-url:http://localhost:8082}")
    private String departmentServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @CircuitBreaker(name = "departmentService", fallbackMethod = "departmentFallback")
    @Retry(name = "departmentService", fallbackMethod = "departmentFallback")
    public boolean validateDepartmentExists(Long departmentId) {
        if (departmentId == null) {
            return false;
        }
        // In local/stub mode if mock is used
        if ("mock".equalsIgnoreCase(departmentServiceUrl) || departmentServiceUrl.contains("localhost")) {
            // Simulated validation: treat department ID > 0 as valid
            log.debug("Department validation in stub/dev mode for departmentId={}", departmentId);
            return departmentId > 0;
        }

        try {
            String url = departmentServiceUrl + "/api/v1/departments/" + departmentId;
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("Error calling department service: {}", e.getMessage());
            throw e;
        }
    }

    public boolean departmentFallback(Long departmentId, Throwable throwable) {
        log.warn("Department service unreachable or failed (fallback triggered for id {}). Allowing operation in degraded mode.", departmentId);
        // Degraded operation: do not block citizens or municipality on temporary directory failure
        return true;
    }
}

