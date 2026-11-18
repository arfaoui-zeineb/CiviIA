package com.civia.reportservice.service;

import com.civia.reportservice.dto.request.ReportImageRequest;
import com.civia.reportservice.dto.response.ReportImageResponse;

import java.util.List;

public interface ReportImageService {
    List<ReportImageResponse> getImagesByReportId(Long reportId);
    ReportImageResponse addImage(Long reportId, ReportImageRequest request);
    void deleteImage(Long reportId, Long imageId);
}
