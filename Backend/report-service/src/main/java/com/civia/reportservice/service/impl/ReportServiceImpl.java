package com.civia.reportservice.service.impl;

import com.civia.reportservice.dto.event.ReportCreatedEventPayload;
import com.civia.reportservice.dto.event.ReportStatusChangedEventPayload;
import com.civia.reportservice.dto.request.CreateReportRequest;
import com.civia.reportservice.dto.request.PatchReportRequest;
import com.civia.reportservice.dto.request.UpdateReportRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportDetailResponse;
import com.civia.reportservice.dto.response.ReportSummaryResponse;
import com.civia.reportservice.exception.BusinessRuleException;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportMapper;
import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportCategory;
import com.civia.reportservice.model.entity.ReportImage;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.repository.ReportCategoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.repository.specification.ReportSpecification;
import com.civia.reportservice.service.OutboxEventService;
import com.civia.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportCategoryRepository categoryRepository;
    private final ReportMapper reportMapper;
    private final OutboxEventService outboxEventService;

    @Override
    @Transactional
    public ReportDetailResponse createReport(CreateReportRequest request) {
        ReportCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ReportCategory not found with id: " + request.categoryId()));

        Report report = reportMapper.toEntity(request, category);

        if (request.imageUrls() != null && !request.imageUrls().isEmpty()) {
            for (String url : request.imageUrls()) {
                if (url != null && !url.isBlank()) {
                    ReportImage image = new ReportImage(report, url.trim(), Instant.now());
                    report.addImage(image);
                }
            }
        }

        Report saved = reportRepository.save(report);

        // Outbox event: ReportCreated in the SAME transaction
        ReportCreatedEventPayload payload = new ReportCreatedEventPayload(
                saved.getId(),
                saved.getTitle(),
                saved.getUserId(),
                saved.getCategory().getId(),
                saved.getCategory().getName(),
                saved.getLatitude(),
                saved.getLongitude(),
                saved.getStatus(),
                saved.getEquipmentId(),
                saved.getCreatedAt()
        );
        outboxEventService.recordEvent(saved.getId(), "ReportCreated", payload);

        log.info("Created report [id={}, title='{}', status={}]", saved.getId(), saved.getTitle(), saved.getStatus());
        return reportMapper.toDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDetailResponse getReportById(Long id) {
        Report report = findReportWithCategoryAndImagesOrThrow(id);
        return reportMapper.toDetailResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportSummaryResponse> getReports(
            ReportStatus status,
            Long categoryId,
            Long userId,
            Long equipmentId,
            Instant createdFrom,
            Instant createdTo,
            String searchTitle,
            Pageable pageable
    ) {
        Specification<Report> spec = ReportSpecification.withFilters(
                status,
                categoryId,
                userId,
                equipmentId,
                createdFrom,
                createdTo,
                searchTitle
        );

        Page<ReportSummaryResponse> page = reportRepository.findAll(spec, pageable)
                .map(reportMapper::toSummaryResponse);

        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public ReportDetailResponse updateReport(Long id, UpdateReportRequest request) {
        Report report = findReportOrThrow(id);

        ReportCategory category = report.getCategory();
        if (!category.getId().equals(request.categoryId())) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("ReportCategory not found with id: " + request.categoryId()));
        }

        reportMapper.updateEntityFromRequest(report, request, category);
        Report updated = reportRepository.save(report);

        log.info("Updated report [id={}, title='{}']", updated.getId(), updated.getTitle());
        return reportMapper.toDetailResponse(updated);
    }

    @Override
    @Transactional
    public ReportDetailResponse patchReport(Long id, PatchReportRequest request) {
        Report report = findReportOrThrow(id);

        ReportCategory newCategory = null;
        if (request.categoryId() != null && !request.categoryId().equals(report.getCategory().getId())) {
            newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("ReportCategory not found with id: " + request.categoryId()));
        }

        reportMapper.patchEntityFromRequest(report, request, newCategory);
        Report updated = reportRepository.save(report);

        log.info("Patched report [id={}]", updated.getId());
        return reportMapper.toDetailResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReport(Long id) {
        Report report = findReportOrThrow(id);
        reportRepository.delete(report);
        log.info("Deleted report [id={}]", id);
    }

    @Override
    @Transactional
    public ReportDetailResponse validateReport(Long id) {
        return transitionStatus(id, ReportStatus.VALIDATED);
    }

    @Override
    @Transactional
    public ReportDetailResponse startReport(Long id) {
        return transitionStatus(id, ReportStatus.IN_PROGRESS);
    }

    @Override
    @Transactional
    public ReportDetailResponse resolveReport(Long id) {
        return transitionStatus(id, ReportStatus.RESOLVED);
    }

    @Override
    @Transactional
    public ReportDetailResponse rejectReport(Long id) {
        return transitionStatus(id, ReportStatus.REJECTED);
    }

    @Override
    @Transactional
    public ReportDetailResponse closeReport(Long id) {
        return transitionStatus(id, ReportStatus.CLOSED);
    }

    private ReportDetailResponse transitionStatus(Long id, ReportStatus targetStatus) {
        Report report = findReportOrThrow(id);
        ReportStatus currentStatus = report.getStatus();

        if (!currentStatus.canTransitionTo(targetStatus)) {
            String message = String.format("Cannot transition report status from '%s' to '%s'. Allowed transitions: SUBMITTED->VALIDATED, SUBMITTED->REJECTED, VALIDATED->IN_PROGRESS, VALIDATED->REJECTED, IN_PROGRESS->RESOLVED, RESOLVED->CLOSED.",
                    currentStatus, targetStatus);
            log.warn("Invalid report status transition attempted [reportId={}, current={}, target={}]", id, currentStatus, targetStatus);
            throw new BusinessRuleException(message);
        }

        report.setStatus(targetStatus);
        Report updated = reportRepository.save(report);

        // Outbox event: ReportStatusChanged in the SAME transaction
        Instant now = Instant.now();
        ReportStatusChangedEventPayload payload = new ReportStatusChangedEventPayload(
                report.getId(),
                currentStatus,
                targetStatus,
                now
        );
        outboxEventService.recordEvent(report.getId(), "ReportStatusChanged", payload);

        log.info("Report status transitioned [reportId={}, oldStatus={}, newStatus={}]", id, currentStatus, targetStatus);
        return reportMapper.toDetailResponse(updated);
    }

    private Report findReportOrThrow(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }

    private Report findReportWithCategoryAndImagesOrThrow(Long id) {
        return reportRepository.findWithCategoryAndImagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }
}
