package com.civia.reportservice.mapper;

import com.civia.reportservice.dto.response.ReportHistoryResponse;
import com.civia.reportservice.model.entity.ReportHistory;
import org.springframework.stereotype.Component;

@Component
public class ReportHistoryMapper {

    public ReportHistoryResponse toResponse(ReportHistory entity) {
        if (entity == null) return null;
        Long reportId = entity.getReportId() != null
                ? entity.getReportId()
                : (entity.getReport() != null ? entity.getReport().getId() : null);

        return new ReportHistoryResponse(
                entity.getId(),
                reportId,
                entity.getOldStatus(),
                entity.getNewStatus(),
                entity.getChangedAt()
        );
    }
}
