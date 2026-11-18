package com.civia.reportservice.service.impl;

import com.civia.reportservice.dto.response.PageResponse;
import com.civia.reportservice.dto.response.ReportHistoryResponse;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportHistoryMapper;
import com.civia.reportservice.repository.ReportHistoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.ReportHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportHistoryServiceImpl implements ReportHistoryService {

    private final ReportHistoryRepository historyRepository;
    private final ReportRepository reportRepository;
    private final ReportHistoryMapper historyMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportHistoryResponse> getHistoryByReportId(Long reportId, Pageable pageable) {
        if (!reportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Report not found with id: " + reportId);
        }

        // Always sort by changedAt ASC as required by the specification
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "changedAt")
        );

        Page<ReportHistoryResponse> page = historyRepository.findByReportId(reportId, sortedPageable)
                .map(historyMapper::toResponse);

        return PageResponse.from(page);
    }
}
