package com.civia.reportservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record ReportImageRequest(
        @NotBlank(message = "Cloudinary URL is required")
        @Size(max = 500, message = "Cloudinary URL must not exceed 500 characters")
        String cloudinaryUrl,

        Instant takenAt
) {}
