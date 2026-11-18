package com.civia.reportservice.mapper;

import com.civia.reportservice.dto.request.ReportImageRequest;
import com.civia.reportservice.dto.response.ReportImageResponse;
import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportImage;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ReportImageMapper {

    public ReportImage toEntity(ReportImageRequest request, Report report) {
        if (request == null) return null;
        Instant takenAt = request.takenAt() != null ? request.takenAt() : Instant.now();
        return new ReportImage(report, request.cloudinaryUrl().trim(), takenAt);
    }

    public ReportImageResponse toResponse(ReportImage entity) {
        if (entity == null) return null;
        Long reportId = entity.getReport() != null ? entity.getReport().getId() : null;
        return new ReportImageResponse(
                entity.getId(),
                reportId,
                entity.getCloudinaryUrl(),
                entity.getTakenAt()
        );
    }
}
