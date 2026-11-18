package com.civia.reportservice.controller;

import com.civia.reportservice.dto.request.CreateReportRequest;
import com.civia.reportservice.dto.request.PatchReportRequest;
import com.civia.reportservice.dto.request.UpdateReportRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.dto.response.ReportDetailResponse;
import com.civia.reportservice.dto.response.ReportSummaryResponse;
import com.civia.reportservice.exception.BusinessRuleException;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportService reportService;

    private ReportDetailResponse detailResponse;
    private ReportSummaryResponse summaryResponse;

    @BeforeEach
    void setUp() {
        ReportCategoryResponse cat = new ReportCategoryResponse(1L, "Voirie");
        detailResponse = new ReportDetailResponse(
                10L,
                "Trou dans la chaussée",
                "Nid de poule profond",
                48.8566,
                2.3522,
                ReportStatus.SUBMITTED,
                100L,
                null,
                Instant.now(),
                cat,
                Collections.emptyList()
        );

        summaryResponse = new ReportSummaryResponse(
                10L,
                "Trou dans la chaussée",
                "Nid de poule profond",
                48.8566,
                2.3522,
                ReportStatus.SUBMITTED,
                100L,
                null,
                Instant.now(),
                cat,
                0
        );
    }

    @Test
    @DisplayName("POST /api/v1/reports - Returns 201 with Location header")
    void createReport_success_returns201() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                "Trou dans la chaussée",
                "Nid de poule profond",
                48.8566,
                2.3522,
                1L,
                100L,
                null,
                null
        );

        when(reportService.createReport(any())).thenReturn(detailResponse);

        mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/reports/10")))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.status", is("SUBMITTED")))
                .andExpect(jsonPath("$.title", is("Trou dans la chaussée")));
    }

    @Test
    @DisplayName("POST /api/v1/reports - Returns 400 when coordinates are out of bounds")
    void createReport_invalidCoordinates_returns400() throws Exception {
        // Latitude 95.0 is invalid [-90; 90]
        CreateReportRequest request = new CreateReportRequest(
                "Trou dans la chaussée",
                "Description",
                95.0,
                2.3522,
                1L,
                100L,
                null,
                null
        );

        mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", not(empty())));

        verify(reportService, never()).createReport(any());
    }

    @Test
    @DisplayName("GET /api/v1/reports - Returns 200 with filtered paginated reports")
    void getReports_returns200() throws Exception {
        PageResponse<ReportSummaryResponse> page = PageResponse.from(
                new PageImpl<>(List.of(summaryResponse), PageRequest.of(0, 20), 1)
        );

        when(reportService.getReports(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/reports")
                        .param("status", "SUBMITTED")
                        .param("search", "Trou")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Trou dans la chaussée")));
    }

    @Test
    @DisplayName("GET /api/v1/reports/{id} - Returns 200 when found")
    void getReportById_found_returns200() throws Exception {
        when(reportService.getReportById(10L)).thenReturn(detailResponse);

        mockMvc.perform(get("/api/v1/reports/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("Trou dans la chaussée")));
    }

    @Test
    @DisplayName("GET /api/v1/reports/{id} - Returns 404 when not found")
    void getReportById_notFound_returns404() throws Exception {
        when(reportService.getReportById(99L))
                .thenThrow(new ResourceNotFoundException("Report not found with id: 99"));

        mockMvc.perform(get("/api/v1/reports/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Report not found with id: 99")));
    }

    @Test
    @DisplayName("PUT /api/v1/reports/{id} - Returns 200")
    void updateReport_returns200() throws Exception {
        UpdateReportRequest request = new UpdateReportRequest(
                "Titre modifie", "Description modifiee", 48.0, 2.0, 1L, null
        );

        when(reportService.updateReport(eq(10L), any())).thenReturn(detailResponse);

        mockMvc.perform(put("/api/v1/reports/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/v1/reports/{id} - Returns 200")
    void patchReport_returns200() throws Exception {
        PatchReportRequest request = new PatchReportRequest("Titre patch", null, null, null, null, null);

        when(reportService.patchReport(eq(10L), any())).thenReturn(detailResponse);

        mockMvc.perform(patch("/api/v1/reports/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/reports/{id} - Returns 204")
    void deleteReport_returns204() throws Exception {
        doNothing().when(reportService).deleteReport(10L);

        mockMvc.perform(delete("/api/v1/reports/10"))
                .andExpect(status().isNoContent());

        verify(reportService).deleteReport(10L);
    }

    // --- Status Transitions ---

    @Test
    @DisplayName("POST /api/v1/reports/{id}/validate - Returns 200")
    void validateReport_returns200() throws Exception {
        when(reportService.validateReport(10L)).thenReturn(detailResponse);

        mockMvc.perform(post("/api/v1/reports/10/validate"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/reports/{id}/start - Returns 200")
    void startReport_returns200() throws Exception {
        when(reportService.startReport(10L)).thenReturn(detailResponse);

        mockMvc.perform(post("/api/v1/reports/10/start"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/reports/{id}/resolve - Returns 200")
    void resolveReport_returns200() throws Exception {
        when(reportService.resolveReport(10L)).thenReturn(detailResponse);

        mockMvc.perform(post("/api/v1/reports/10/resolve"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/reports/{id}/reject - Returns 200")
    void rejectReport_returns200() throws Exception {
        when(reportService.rejectReport(10L)).thenReturn(detailResponse);

        mockMvc.perform(post("/api/v1/reports/10/reject"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/reports/{id}/close - Returns 200")
    void closeReport_returns200() throws Exception {
        when(reportService.closeReport(10L)).thenReturn(detailResponse);

        mockMvc.perform(post("/api/v1/reports/10/close"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/reports/{id}/resolve - Returns 422 when invalid status transition")
    void invalidTransition_returns422() throws Exception {
        when(reportService.resolveReport(10L))
                .thenThrow(new BusinessRuleException("Cannot transition report status from 'SUBMITTED' to 'RESOLVED'"));

        mockMvc.perform(post("/api/v1/reports/10/resolve"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.message", containsString("Cannot transition report status from 'SUBMITTED' to 'RESOLVED'")));
    }
}
