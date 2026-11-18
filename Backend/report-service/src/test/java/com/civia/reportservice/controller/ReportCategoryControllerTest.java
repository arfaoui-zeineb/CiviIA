package com.civia.reportservice.controller;

import com.civia.reportservice.dto.request.ReportCategoryRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.exception.ConflictException;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.service.ReportCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportCategoryController.class)
class ReportCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportCategoryService categoryService;

    @Test
    @DisplayName("GET /api/v1/report-categories - Returns 200 with paginated categories")
    void getAllCategories_returns200() throws Exception {
        ReportCategoryResponse cat = new ReportCategoryResponse(1L, "Voirie");
        PageResponse<ReportCategoryResponse> page = PageResponse.from(
                new PageImpl<>(List.of(cat), PageRequest.of(0, 20), 1)
        );

        when(categoryService.getAllCategories(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/report-categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Voirie")));
    }

    @Test
    @DisplayName("GET /api/v1/report-categories/{id} - Returns 200 when found")
    void getCategoryById_found_returns200() throws Exception {
        ReportCategoryResponse cat = new ReportCategoryResponse(1L, "Voirie");
        when(categoryService.getCategoryById(1L)).thenReturn(cat);

        mockMvc.perform(get("/api/v1/report-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Voirie")));
    }

    @Test
    @DisplayName("GET /api/v1/report-categories/{id} - Returns 404 when not found")
    void getCategoryById_notFound_returns404() throws Exception {
        when(categoryService.getCategoryById(99L))
                .thenThrow(new ResourceNotFoundException("ReportCategory not found with id: 99"));

        mockMvc.perform(get("/api/v1/report-categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("ReportCategory not found with id: 99")));
    }

    @Test
    @DisplayName("POST /api/v1/report-categories - Returns 201 with Location header")
    void createCategory_success_returns201() throws Exception {
        ReportCategoryRequest request = new ReportCategoryRequest("Éclairage");
        ReportCategoryResponse response = new ReportCategoryResponse(2L, "Éclairage");

        when(categoryService.createCategory(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/report-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/report-categories/2")))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Éclairage")));
    }

    @Test
    @DisplayName("POST /api/v1/report-categories - Returns 400 when name is blank")
    void createCategory_validationError_returns400() throws Exception {
        ReportCategoryRequest request = new ReportCategoryRequest("");

        mockMvc.perform(post("/api/v1/report-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", not(empty())));

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    @DisplayName("POST /api/v1/report-categories - Returns 409 when category name already exists")
    void createCategory_duplicate_returns409() throws Exception {
        ReportCategoryRequest request = new ReportCategoryRequest("Voirie");

        when(categoryService.createCategory(any()))
                .thenThrow(new ConflictException("Category with name 'Voirie' already exists"));

        mockMvc.perform(post("/api/v1/report-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("DELETE /api/v1/report-categories/{id} - Returns 204 when deleted")
    void deleteCategory_success_returns204() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/v1/report-categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/report-categories/{id} - Returns 409 when category is in use")
    void deleteCategory_inUse_returns409() throws Exception {
        doThrow(new ConflictException("Cannot delete category because it is in use"))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/v1/report-categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("in use")));
    }
}
