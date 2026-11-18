package com.civia.reportservice.controller;

import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportHistoryResponse;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.service.ReportHistoryService;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportHistoryController.class)
class ReportHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportHistoryService historyService;

    @Test
    @DisplayName("GET /api/v1/reports/{reportId}/history - Returns 200 with paginated history")
    void getHistory_returns200() throws Exception {
        ReportHistoryResponse entry = new ReportHistoryResponse(
                1L, 10L, null, ReportStatus.SUBMITTED, Instant.now()
        );
        PageResponse<ReportHistoryResponse> page = PageResponse.from(
                new PageImpl<>(List.of(entry), PageRequest.of(0, 20), 1)
        );

        when(historyService.getHistoryByReportId(eq(10L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/reports/10/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].newStatus", is("SUBMITTED")));
    }
}
