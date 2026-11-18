package com.civicpulse.communication.integration.impl;

import com.civicpulse.communication.integration.AlertConsumer;
import com.civicpulse.communication.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertConsumerImpl implements AlertConsumer {

    private final AnnouncementService announcementService;
    // Set for idempotent processing of alerts
    private final Set<String> processedAlertIds = ConcurrentHashMap.newKeySet();

    @Override
    public void handleAlertRaised(String alertId, String title, String description, Long departmentId, Long categoryId) {
        if (alertId != null && !processedAlertIds.add(alertId)) {
            log.info("Alert with ID {} already processed, skipping duplicate event", alertId);
            return;
        }

        log.info("Processing AlertRaised event: id={}, title={}", alertId, title);
        announcementService.createDraftFromAlert(
                alertId,
                "[ALERTE] " + title,
                description,
                departmentId,
                categoryId != null ? categoryId : 1L
        );
    }
}

