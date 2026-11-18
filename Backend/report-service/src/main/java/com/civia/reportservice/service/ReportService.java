package com.civia.reportservice.service;

import com.civia.reportservice.dto.request.CreateReportRequest;
import com.civia.reportservice.dto.request.PatchReportRequest;
import com.civia.reportservice.dto.request.UpdateReportRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportDetailResponse;
import com.civia.reportservice.dto.response.ReportSummaryResponse;
import com.civia.reportservice.model.enums.ReportStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface ReportService {

    ReportDetailResponse createReport(CreateReportRequest request);

    ReportDetailResponse getReportById(Long id);

    PageResponse<ReportSummaryResponse> getReports(
            ReportStatus status,
            Long categoryId,
            Long userId,
            Long equipmentId,
            Instant createdFrom,
            Instant createdTo,
            String searchTitle,
            Pageable pageable
    );

    ReportDetailResponse updateReport(Long id, UpdateReportRequest request);

    ReportDetailResponse patchReport(Long id, PatchReportRequest request);

    void deleteReport(Long id);

    ReportDetailResponse validateReport(Long id);

    ReportDetailResponse startReport(Long id);

    ReportDetailResponse resolveReport(Long id);

    ReportDetailResponse rejectReport(Long id);

    ReportDetailResponse closeReport(Long id);
}
