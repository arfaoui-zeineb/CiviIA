package com.civia.reportservice.service;

import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportHistoryResponse;
import org.springframework.data.domain.Pageable;

public interface ReportHistoryService {
    PageResponse<ReportHistoryResponse> getHistoryByReportId(Long reportId, Pageable pageable);
}
