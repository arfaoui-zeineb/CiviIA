package com.civia.reportservice.dto.event;

import com.civia.reportservice.model.enums.ReportStatus;

import java.time.Instant;

public record ReportStatusChangedEventPayload(
        Long reportId,
        ReportStatus oldStatus,
        ReportStatus newStatus,
        Instant occurredAt
) {}
