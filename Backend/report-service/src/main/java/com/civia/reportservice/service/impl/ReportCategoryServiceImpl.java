package com.civia.reportservice.service.impl;

import com.civia.reportservice.dto.request.ReportCategoryRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.exception.ConflictException;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportCategoryMapper;
import com.civia.reportservice.model.entity.ReportCategory;
import com.civia.reportservice.repository.ReportCategoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.ReportCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportCategoryServiceImpl implements ReportCategoryService {

    private final ReportCategoryRepository categoryRepository;
    private final ReportRepository reportRepository;
    private final ReportCategoryMapper categoryMapper;

    @Override
    @Transactional
    public ReportCategoryResponse createCategory(ReportCategoryRequest request) {
        String trimmedName = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new ConflictException("Category with name '" + trimmedName + "' already exists");
        }

        ReportCategory category = categoryMapper.toEntity(request);
        ReportCategory saved = categoryRepository.save(category);
        log.info("Created report category [id={}, name='{}']", saved.getId(), saved.getName());
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportCategoryResponse getCategoryById(Long id) {
        ReportCategory category = findCategoryOrThrow(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportCategoryResponse> getAllCategories(Pageable pageable) {
        Page<ReportCategoryResponse> page = categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public ReportCategoryResponse updateCategory(Long id, ReportCategoryRequest request) {
        ReportCategory category = findCategoryOrThrow(id);
        String trimmedName = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new ConflictException("Category with name '" + trimmedName + "' already exists");
        }

        categoryMapper.updateEntityFromRequest(category, request);
        ReportCategory updated = categoryRepository.save(category);
        log.info("Updated report category [id={}, name='{}']", updated.getId(), updated.getName());
        return categoryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ReportCategory category = findCategoryOrThrow(id);

        if (reportRepository.existsByCategoryId(id)) {
            throw new ConflictException("Cannot delete category with id " + id + " because it is currently associated with existing reports");
        }

        categoryRepository.delete(category);
        log.info("Deleted report category [id={}, name='{}']", category.getId(), category.getName());
    }

    private ReportCategory findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReportCategory not found with id: " + id));
    }
}
