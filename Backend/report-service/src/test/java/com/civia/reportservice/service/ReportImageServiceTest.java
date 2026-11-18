package com.civia.reportservice.service;

import com.civia.reportservice.dto.request.ReportImageRequest;
import com.civia.reportservice.dto.response.ReportImageResponse;
import com.civia.reportservice.exception.ResourceNotFoundException;
import com.civia.reportservice.mapper.ReportImageMapper;
import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportImage;
import com.civia.reportservice.repository.ReportImageRepository;
import com.civia.reportservice.repository.ReportRepository;
import com.civia.reportservice.service.impl.ReportImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportImageServiceTest {

    @Mock
    private ReportImageRepository imageRepository;

    @Mock
    private ReportRepository reportRepository;

    @Spy
    private ReportImageMapper imageMapper = new ReportImageMapper();

    @InjectMocks
    private ReportImageServiceImpl imageService;

    private Report report;
    private ReportImage image;

    @BeforeEach
    void setUp() {
        report = new Report();
        report.setId(10L);

        image = new ReportImage(report, "https://cloudinary.com/test.jpg", Instant.now());
        image.setId(1L);
    }

    @Test
    @DisplayName("getImagesByReportId - Success")
    void getImagesByReportId_success() {
        when(reportRepository.existsById(10L)).thenReturn(true);
        when(imageRepository.findByReportId(10L)).thenReturn(List.of(image));

        List<ReportImageResponse> result = imageService.getImagesByReportId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).cloudinaryUrl()).isEqualTo("https://cloudinary.com/test.jpg");
    }

    @Test
    @DisplayName("getImagesByReportId - Throws ResourceNotFoundException when report does not exist")
    void getImagesByReportId_reportNotFound() {
        when(reportRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> imageService.getImagesByReportId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Report not found with id: 99");
    }

    @Test
    @DisplayName("addImage - Success attaches image to report")
    void addImage_success() {
        ReportImageRequest request = new ReportImageRequest("https://cloudinary.com/new.jpg", Instant.now());

        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));
        when(imageRepository.save(any(ReportImage.class))).thenAnswer(i -> {
            ReportImage img = i.getArgument(0);
            img.setId(2L);
            return img;
        });

        ReportImageResponse response = imageService.addImage(10L, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.cloudinaryUrl()).isEqualTo("https://cloudinary.com/new.jpg");
    }

    @Test
    @DisplayName("deleteImage - Success removes image")
    void deleteImage_success() {
        when(reportRepository.existsById(10L)).thenReturn(true);
        when(imageRepository.findByIdAndReportId(1L, 10L)).thenReturn(Optional.of(image));
        doNothing().when(imageRepository).delete(image);

        imageService.deleteImage(10L, 1L);

        verify(imageRepository).delete(image);
    }

    @Test
    @DisplayName("deleteImage - Throws ResourceNotFoundException when image not found for report")
    void deleteImage_notFound() {
        when(reportRepository.existsById(10L)).thenReturn(true);
        when(imageRepository.findByIdAndReportId(99L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.deleteImage(10L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ReportImage with id 99 not found for report 10");
    }
}
