package com.civia.reportservice.dto.event;

import com.civia.reportservice.model.enums.ReportStatus;

import java.time.Instant;

public record ReportCreatedEventPayload(
        Long reportId,
        String title,
        Long userId,
        Long categoryId,
        String categoryName,
        Double latitude,
        Double longitude,
        ReportStatus status,
        Long equipmentId,
        Instant createdAt
) {}
