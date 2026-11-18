package com.civia.reportservice.service;

import com.civia.reportservice.dto.event.ReportCreatedEventPayload;
import com.civia.reportservice.dto.event.ReportStatusChangedEventPayload;
import com.civia.reportservice.dto.request.CreateReportRequest;
import com.civia.reportservice.dto.request.PatchReportRequest;
import com.civia.reportservice.dto.request.UpdateReportRequest;
import com.civia.reportservice.dto.response.ReportCategoryResponse;
import com.civia.reportservice.dto.response.ReportDetailResponse;
import com.civia.reportservice.exception.BusinessRuleException;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportCategoryMapper;
import com.civia.reportservice.mapper.ReportImageMapper;
import com.civia.reportservice.mapper.ReportMapper;
import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportCategory;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.repository.ReportCategoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportCategoryRepository categoryRepository;

    @Mock
    private OutboxEventService outboxEventService;

    @Spy
    private ReportCategoryMapper categoryMapper = new ReportCategoryMapper();

    @Spy
    private ReportImageMapper imageMapper = new ReportImageMapper();

    private ReportMapper reportMapper;
    private ReportServiceImpl reportService;

    private ReportCategory category;
    private Report sampleReport;

    @BeforeEach
    void setUp() {
        reportMapper = new ReportMapper(categoryMapper, imageMapper);
        reportService = new ReportServiceImpl(reportRepository, categoryRepository, reportMapper, outboxEventService);

        category = new ReportCategory(1L, "Nid-de-poule");
        sampleReport = new Report();
        sampleReport.setId(10L);
        sampleReport.setTitle("Gros trou sur la chaussée");
        sampleReport.setDescription("Trou dangereux pour les deux-roues");
        sampleReport.setLatitude(48.8566);
        sampleReport.setLongitude(2.3522);
        sampleReport.setUserId(100L);
        sampleReport.setCategory(category);
        sampleReport.setStatus(ReportStatus.SUBMITTED);
        sampleReport.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("createReport - Success should force SUBMITTED status and emit ReportCreated outbox event")
    void createReport_success() {
        CreateReportRequest request = new CreateReportRequest(
                "Gros trou sur la chaussée",
                "Trou dangereux pour les deux-roues",
                48.8566,
                2.3522,
                1L,
                100L,
                null,
                List.of("https://res.cloudinary.com/demo/image/upload/v1/sample.jpg")
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReportDetailResponse response = reportService.createReport(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(ReportStatus.SUBMITTED);
        assertThat(response.title()).isEqualTo("Gros trou sur la chaussée");
        assertThat(response.category().name()).isEqualTo("Nid-de-poule");

        verify(outboxEventService).recordEvent(eq(10L), eq("ReportCreated"), any(ReportCreatedEventPayload.class));
    }

    @Test
    @DisplayName("createReport - Throws ResourceNotFoundException when category does not exist")
    void createReport_categoryNotFound() {
        CreateReportRequest request = new CreateReportRequest(
                "Titre", "Description", 48.0, 2.0, 999L, 100L, null, null
        );

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.createReport(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ReportCategory not found with id: 999");

        verify(reportRepository, never()).save(any());
        verify(outboxEventService, never()).recordEvent(any(), any(), any());
    }

    // --- Status Transition Rules ---

    @Test
    @DisplayName("validateReport - Allowed: SUBMITTED -> VALIDATED")
    void validateReport_allowed() {
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDetailResponse response = reportService.validateReport(10L);

        assertThat(response.status()).isEqualTo(ReportStatus.VALIDATED);
        verify(outboxEventService).recordEvent(eq(10L), eq("ReportStatusChanged"), any(ReportStatusChangedEventPayload.class));
    }

    @Test
    @DisplayName("startReport - Allowed: VALIDATED -> IN_PROGRESS")
    void startReport_allowed() {
        sampleReport.setStatus(ReportStatus.VALIDATED);
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDetailResponse response = reportService.startReport(10L);

        assertThat(response.status()).isEqualTo(ReportStatus.IN_PROGRESS);
        verify(outboxEventService).recordEvent(eq(10L), eq("ReportStatusChanged"), any(ReportStatusChangedEventPayload.class));
    }

    @Test
    @DisplayName("resolveReport - Allowed: IN_PROGRESS -> RESOLVED")
    void resolveReport_allowed() {
        sampleReport.setStatus(ReportStatus.IN_PROGRESS);
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDetailResponse response = reportService.resolveReport(10L);

        assertThat(response.status()).isEqualTo(ReportStatus.RESOLVED);
        verify(outboxEventService).recordEvent(eq(10L), eq("ReportStatusChanged"), any(ReportStatusChangedEventPayload.class));
    }

    @Test
    @DisplayName("closeReport - Allowed: RESOLVED -> CLOSED")
    void closeReport_allowed() {
        sampleReport.setStatus(ReportStatus.RESOLVED);
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDetailResponse response = reportService.closeReport(10L);

        assertThat(response.status()).isEqualTo(ReportStatus.CLOSED);
        verify(outboxEventService).recordEvent(eq(10L), eq("ReportStatusChanged"), any(ReportStatusChangedEventPayload.class));
    }

    @Test
    @DisplayName("rejectReport - Allowed from SUBMITTED or VALIDATED")
    void rejectReport_allowed() {
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDetailResponse response = reportService.rejectReport(10L);
        assertThat(response.status()).isEqualTo(ReportStatus.REJECTED);

        // Also test from VALIDATED
        sampleReport.setStatus(ReportStatus.VALIDATED);
        ReportDetailResponse response2 = reportService.rejectReport(10L);
        assertThat(response2.status()).isEqualTo(ReportStatus.REJECTED);
    }

    @Test
    @DisplayName("Status transition - Disallowed transitions throw BusinessRuleException (422)")
    void statusTransition_disallowed_throwsBusinessRuleException() {
        // Disallowed: SUBMITTED -> IN_PROGRESS
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        assertThatThrownBy(() -> reportService.startReport(10L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot transition report status from 'SUBMITTED' to 'IN_PROGRESS'");

        // Disallowed: SUBMITTED -> CLOSED
        assertThatThrownBy(() -> reportService.closeReport(10L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot transition report status from 'SUBMITTED' to 'CLOSED'");

        // Disallowed: CLOSED -> any
        sampleReport.setStatus(ReportStatus.CLOSED);
        assertThatThrownBy(() -> reportService.validateReport(10L))
                .isInstanceOf(BusinessRuleException.class);

        // Disallowed: REJECTED -> any
        sampleReport.setStatus(ReportStatus.REJECTED);
        assertThatThrownBy(() -> reportService.startReport(10L))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("updateReport - Modifies fields without altering status")
    void updateReport_success() {
        UpdateReportRequest request = new UpdateReportRequest(
                "Nouveau titre", "Nouvelle description", 48.9, 2.4, 1L, 42L
        );

        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        ReportDetailResponse response = reportService.updateReport(10L, request);

        assertThat(response.title()).isEqualTo("Nouveau titre");
        assertThat(response.description()).isEqualTo("Nouvelle description");
        assertThat(response.latitude()).isEqualTo(48.9);
        assertThat(response.longitude()).isEqualTo(2.4);
        assertThat(response.equipmentId()).isEqualTo(42L);
        assertThat(response.status()).isEqualTo(ReportStatus.SUBMITTED); // Unchanged!
    }

    @Test
    @DisplayName("deleteReport - Success")
    void deleteReport_success() {
        when(reportRepository.findById(10L)).thenReturn(Optional.of(sampleReport));
        doNothing().when(reportRepository).delete(sampleReport);

        reportService.deleteReport(10L);

        verify(reportRepository).delete(sampleReport);
    }
}
