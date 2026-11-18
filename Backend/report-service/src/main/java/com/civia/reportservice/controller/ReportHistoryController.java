package com.civia.reportservice.controller;

import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportHistoryResponse;
import com.civia.reportservice.service.ReportHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports/{reportId}/history")
@RequiredArgsConstructor
@Tag(name = "Report History", description = "Read-only access to status change audit history")
public class ReportHistoryController {

    private final ReportHistoryService historyService;

    @GetMapping
    @Operation(summary = "Get status history of a report", description = "Read-only audit history of report status changes, ordered by changedAt ascending, paginated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of history records"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<PageResponse<ReportHistoryResponse>> getHistory(
            @PathVariable Long reportId,
            @ParameterObject @PageableDefault(size = 20, sort = "changedAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(historyService.getHistoryByReportId(reportId, pageable));
    }
}
