package com.civia.reportservice.repository;

import com.civia.reportservice.model.entity.ReportImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {
    List<ReportImage> findByReportId(Long reportId);
    Page<ReportImage> findByReportId(Long reportId, Pageable pageable);
    Optional<ReportImage> findByIdAndReportId(Long id, Long reportId);
    boolean existsByIdAndReportId(Long id, Long reportId);
}
