package com.civicpulse.communication.integration;

public interface DepartmentClient {
    /**
     * Checks if a department exists in the Identity/Organization service.
     * Returns true if valid or if in degraded mode.
     */
    boolean validateDepartmentExists(Long departmentId);
}

