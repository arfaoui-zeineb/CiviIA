package com.civia.reportservice.service;

import com.civia.reportservice.dto.request.ReportCategoryRequest;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.exception.ConflictException;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportCategoryMapper;
import com.civia.reportservice.model.entity.ReportCategory;
import com.civia.reportservice.repository.ReportCategoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.impl.ReportCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportCategoryServiceTest {

    @Mock
    private ReportCategoryRepository categoryRepository;

    @Mock
    private ReportRepository reportRepository;

    @Spy
    private ReportCategoryMapper categoryMapper = new ReportCategoryMapper();

    @InjectMocks
    private ReportCategoryServiceImpl categoryService;

    private ReportCategory category;

    @BeforeEach
    void setUp() {
        category = new ReportCategory(1L, "Voirie");
    }

    @Test
    @DisplayName("createCategory - Success when name is unique")
    void createCategory_success() {
        ReportCategoryRequest request = new ReportCategoryRequest("Voirie");

        when(categoryRepository.existsByNameIgnoreCase("Voirie")).thenReturn(false);
        when(categoryRepository.save(any(ReportCategory.class))).thenReturn(category);

        ReportCategoryResponse response = categoryService.createCategory(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Voirie");
    }

    @Test
    @DisplayName("createCategory - Throws ConflictException (409) when category name already exists")
    void createCategory_duplicateName_throwsConflictException() {
        ReportCategoryRequest request = new ReportCategoryRequest("Voirie");

        when(categoryRepository.existsByNameIgnoreCase("Voirie")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteCategory - Throws ConflictException (409) when category is associated with reports")
    void deleteCategory_inUse_throwsConflictException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(reportRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("associated with existing reports");

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteCategory - Throws ResourceNotFoundException (404) when category does not exist")
    void deleteCategory_notFound_throwsResourceNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ReportCategory not found with id: 99");
    }

    @Test
    @DisplayName("deleteCategory - Success when category exists and is not used")
    void deleteCategory_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(reportRepository.existsByCategoryId(1L)).thenReturn(false);
        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }
}
