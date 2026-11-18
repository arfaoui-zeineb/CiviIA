package com.civia.reportservice.service;

import com.civia.reportservice.dto.request.ReportCategoryRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import org.springframework.data.domain.Pageable;

public interface ReportCategoryService {
    ReportCategoryResponse createCategory(ReportCategoryRequest request);
    ReportCategoryResponse getCategoryById(Long id);
    PageResponse<ReportCategoryResponse> getAllCategories(Pageable pageable);
    ReportCategoryResponse updateCategory(Long id, ReportCategoryRequest request);
    void deleteCategory(Long id);
}
