package com.civia.reportservice.mapper;

import com.civia.reportservice.dto.request.CreateReportRequest;
import com.civia.reportservice.dto.request.PatchReportRequest;
import com.civia.reportservice.dto.request.UpdateReportRequest;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.dto.response.ReportDetailResponse;
import com.civia.reportservice.dto.response.ReportImageResponse;
import com.civia.reportservice.dto.response.ReportSummaryResponse;
import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportCategory;
import com.civia.reportservice.model.enums.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    private final ReportCategoryMapper categoryMapper;
    private final ReportImageMapper imageMapper;

    public Report toEntity(CreateReportRequest request, ReportCategory category) {
        if (request == null) return null;
        Report report = new Report();
        report.setTitle(request.title().trim());
        report.setDescription(request.description().trim());
        report.setLatitude(request.latitude());
        report.setLongitude(request.longitude());
        report.setCategory(category);
        report.setUserId(request.userId());
        report.setEquipmentId(request.equipmentId());
        // Status is forced to SUBMITTED
        report.setStatus(ReportStatus.SUBMITTED);
        return report;
    }

    public ReportSummaryResponse toSummaryResponse(Report report) {
        if (report == null) return null;
        ReportCategoryResponse categoryResponse = categoryMapper.toResponse(report.getCategory());
        int imagesCount = report.getImages() != null ? report.getImages().size() : 0;
        return new ReportSummaryResponse(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getLatitude(),
                report.getLongitude(),
                report.getStatus(),
                report.getUserId(),
                report.getEquipmentId(),
                report.getCreatedAt(),
                categoryResponse,
                imagesCount
        );
    }

    public ReportDetailResponse toDetailResponse(Report report) {
        if (report == null) return null;
        ReportCategoryResponse categoryResponse = categoryMapper.toResponse(report.getCategory());
        List<ReportImageResponse> imageResponses = report.getImages() != null
                ? report.getImages().stream().map(imageMapper::toResponse).toList()
                : Collections.emptyList();

        return new ReportDetailResponse(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getLatitude(),
                report.getLongitude(),
                report.getStatus(),
                report.getUserId(),
                report.getEquipmentId(),
                report.getCreatedAt(),
                categoryResponse,
                imageResponses
        );
    }

    public void updateEntityFromRequest(Report report, UpdateReportRequest request, ReportCategory category) {
        report.setTitle(request.title().trim());
        report.setDescription(request.description().trim());
        report.setLatitude(request.latitude());
        report.setLongitude(request.longitude());
        report.setCategory(category);
        report.setEquipmentId(request.equipmentId());
        // Status is deliberately untouched
    }

    public void patchEntityFromRequest(Report report, PatchReportRequest request, ReportCategory newCategory) {
        if (StringUtils.hasText(request.title())) {
            report.setTitle(request.title().trim());
        }
        if (StringUtils.hasText(request.description())) {
            report.setDescription(request.description().trim());
        }
        if (request.latitude() != null) {
            report.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            report.setLongitude(request.longitude());
        }
        if (newCategory != null) {
            report.setCategory(newCategory);
        }
        if (request.equipmentId() != null) {
            report.setEquipmentId(request.equipmentId());
        }
        // Status is deliberately untouched
    }
}
