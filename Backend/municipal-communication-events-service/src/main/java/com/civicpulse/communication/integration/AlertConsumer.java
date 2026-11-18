package com.civicpulse.communication.integration;

public interface AlertConsumer {
    void handleAlertRaised(String alertId, String title, String description, Long departmentId, Long categoryId);
}

