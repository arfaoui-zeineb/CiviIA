package com.civia.reportservice.mapper;

import com.civia.reportservice.dto.request.ReportCategoryRequest;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.model.entity.ReportCategory;
import org.springframework.stereotype.Component;

@Component
public class ReportCategoryMapper {

    public ReportCategory toEntity(ReportCategoryRequest request) {
        if (request == null) return null;
        return new ReportCategory(request.name().trim());
    }

    public ReportCategoryResponse toResponse(ReportCategory entity) {
        if (entity == null) return null;
        return new ReportCategoryResponse(entity.getId(), entity.getName());
    }

    public void updateEntityFromRequest(ReportCategory entity, ReportCategoryRequest request) {
        if (entity != null && request != null) {
            entity.setName(request.name().trim());
        }
    }
}
