package com.civia.reportservice.dto.response;

import com.civia.reportservice.model.enums.ReportStatus;

import java.time.Instant;

public record ReportSummaryResponse(
        Long id,
        String title,
        String description,
        Double latitude,
        Double longitude,
        ReportStatus status,
        Long userId,
        Long equipmentId,
        Instant createdAt,
        ReportCategoryResponse category,
        int imagesCount
) {}
