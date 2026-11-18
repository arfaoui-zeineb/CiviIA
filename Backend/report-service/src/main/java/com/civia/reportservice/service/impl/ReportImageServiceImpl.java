package com.civia.reportservice.service.impl;

import com.civia.reportservice.dto.request.ReportImageRequest;
import com.civia.reportservice.dto.response.ReportImageResponse;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportImageMapper;
import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportImage;
import com.civia.reportservice.repository.ReportImageRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.ReportImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportImageServiceImpl implements ReportImageService {

    private final ReportImageRepository reportImageRepository;
    private final ReportRepository reportRepository;
    private final ReportImageMapper reportImageMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReportImageResponse> getImagesByReportId(Long reportId) {
        verifyReportExists(reportId);
        return reportImageRepository.findByReportId(reportId).stream()
                .map(reportImageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReportImageResponse addImage(Long reportId, ReportImageRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        ReportImage image = reportImageMapper.toEntity(request, report);
        report.addImage(image);
        ReportImage saved = reportImageRepository.save(image);

        log.info("Added image [id={}] to report [id={}]", saved.getId(), reportId);
        return reportImageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteImage(Long reportId, Long imageId) {
        verifyReportExists(reportId);

        ReportImage image = reportImageRepository.findByIdAndReportId(imageId, reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("ReportImage with id %d not found for report %d", imageId, reportId)));

        Report report = image.getReport();
        if (report != null) {
            report.removeImage(image);
        }
        reportImageRepository.delete(image);
        log.info("Deleted image [id={}] from report [id={}]", imageId, reportId);
    }

    private void verifyReportExists(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Report not found with id: " + reportId);
        }
    }
}
