package com.civia.reportservice.repository;

import com.civia.reportservice.model.entity.Report;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    boolean existsByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Report> findWithCategoryAndImagesById(Long id);
}
