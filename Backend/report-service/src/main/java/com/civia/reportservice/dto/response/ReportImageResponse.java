package com.civia.reportservice.dto.response;

import java.time.Instant;

public record ReportImageResponse(
        Long id,
        Long reportId,
        String cloudinaryUrl,
        Instant takenAt
) {}
