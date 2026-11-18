package com.civia.reportservice.service;

import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportHistoryResponse;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportHistoryMapper;
import com.civia.reportservice.model.entity.ReportHistory;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.repository.ReportHistoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.impl.ReportHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportHistoryServiceTest {

    @Mock
    private ReportHistoryRepository historyRepository;

    @Mock
    private ReportRepository reportRepository;

    @Spy
    private ReportHistoryMapper historyMapper = new ReportHistoryMapper();

    @InjectMocks
    private ReportHistoryServiceImpl historyService;

    private ReportHistory historyEntry;

    @BeforeEach
    void setUp() {
        historyEntry = new ReportHistory(1L, 10L, null, ReportStatus.SUBMITTED, Instant.now());
    }

    @Test
    @DisplayName("getHistoryByReportId - Success returns sorted history")
    void getHistoryByReportId_success() {
        when(reportRepository.existsById(10L)).thenReturn(true);
        PageImpl<ReportHistory> page = new PageImpl<>(List.of(historyEntry), PageRequest.of(0, 20, Sort.by("changedAt").ascending()), 1);
        when(historyRepository.findByReportId(eq(10L), any(Pageable.class))).thenReturn(page);

        PageResponse<ReportHistoryResponse> response = historyService.getHistoryByReportId(10L, PageRequest.of(0, 20));

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).newStatus()).isEqualTo(ReportStatus.SUBMITTED);
    }

    @Test
    @DisplayName("getHistoryByReportId - Throws ResourceNotFoundException when report does not exist")
    void getHistoryByReportId_reportNotFound() {
        when(reportRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> historyService.getHistoryByReportId(99L, PageRequest.of(0, 20)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Report not found with id: 99");
    }
}
