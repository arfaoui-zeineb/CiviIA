package com.civicpulse.communication.service;

import com.civicpulse.communication.dto.request.CategoryRequest;
import com.civicpulse.communication.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}

