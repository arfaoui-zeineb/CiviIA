package com.civia.reportservice.dto.response;

import com.civia.reportservice.model.enums.ReportStatus;

import java.time.Instant;

public record ReportHistoryResponse(
        Long id,
        Long reportId,
        ReportStatus oldStatus,
        ReportStatus newStatus,
        Instant changedAt
) {}
