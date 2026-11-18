package com.civia.reportservice.controller;

import com.civia.reportservice.dto.request.CreateReportRequest;
import com.civia.reportservice.dto.request.PatchReportRequest;
import com.civia.reportservice.dto.request.UpdateReportRequest;
import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportDetailResponse;
import com.civia.reportservice.dto.response.ReportSummaryResponse;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for managing citizen reports and status lifecycle")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "Create a new report", description = "Creates a report in SUBMITTED status and records a ReportCreated outbox event.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Report created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ReportDetailResponse> createReport(@Valid @RequestBody CreateReportRequest request) {
        ReportDetailResponse created = reportService.createReport(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "Search and list reports", description = "Paginated list of reports with optional filters on status, category, user, equipment, date range and title keyword.")
    @ApiResponse(responseCode = "200", description = "Page of reports")
    public ResponseEntity<PageResponse<ReportSummaryResponse>> getReports(
            @Parameter(description = "Filter by status") @RequestParam(required = false) ReportStatus status,
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by citizen user ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter by equipment ID") @RequestParam(required = false) Long equipmentId,
            @Parameter(description = "Created from (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @Parameter(description = "Created to (ISO 8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @Parameter(description = "Search keyword in title") @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getReports(status, categoryId, userId, equipmentId, createdFrom, createdTo, search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get report details by ID", description = "Returns full details of a report including category and images.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report found"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ReportDetailResponse> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace report details", description = "Replaces title, description, coordinates, category, and equipment. Status cannot be modified.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Report or Category not found")
    })
    public ResponseEntity<ReportDetailResponse> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReportRequest request
    ) {
        return ResponseEntity.ok(reportService.updateReport(id, request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update report", description = "Partially updates non-null fields. Status cannot be modified.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report patched"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Report or Category not found")
    })
    public ResponseEntity<ReportDetailResponse> patchReport(
            @PathVariable Long id,
            @Valid @RequestBody PatchReportRequest request
    ) {
        return ResponseEntity.ok(reportService.patchReport(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete report by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Report deleted"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    // --- Status Transitions ---

    @PostMapping("/{id}/validate")
    @Operation(summary = "Validate report", description = "Transitions status from SUBMITTED to VALIDATED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report validated"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    public ResponseEntity<ReportDetailResponse> validateReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.validateReport(id));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start report intervention", description = "Transitions status from VALIDATED to IN_PROGRESS.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intervention started"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    public ResponseEntity<ReportDetailResponse> startReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.startReport(id));
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolve report", description = "Transitions status from IN_PROGRESS to RESOLVED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report resolved"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    public ResponseEntity<ReportDetailResponse> resolveReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.resolveReport(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject report", description = "Transitions status from SUBMITTED or VALIDATED to REJECTED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report rejected"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    public ResponseEntity<ReportDetailResponse> rejectReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.rejectReport(id));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close report", description = "Transitions status from RESOLVED to CLOSED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report closed"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    public ResponseEntity<ReportDetailResponse> closeReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.closeReport(id));
    }
}
