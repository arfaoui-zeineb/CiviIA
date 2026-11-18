package com.civia.reportservice.integration;

import com.civia.reportservice.model.entity.Report;
import com.civia.reportservice.model.entity.ReportCategory;
import com.civia.reportservice.model.entity.ReportHistory;
import com.civia.reportservice.model.enums.ReportStatus;
import com.civia.reportservice.repository.ReportCategoryRepository;
import com.civia.reportservice.repository.ReportHistoryRepository;
import com.civia.reportservice.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@EnabledIf("isDockerAvailable")
class ReportPostgresTriggerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("ReportingService")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (isDockerAvailable()) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
            registry.add("spring.flyway.enabled", () -> "true");
        }
    }

    static boolean isDockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportCategoryRepository categoryRepository;

    @Autowired
    private ReportHistoryRepository historyRepository;

    @Test
    @DisplayName("PostgreSQL Trigger - Automatically records history on INSERT and status UPDATE")
    void verifyTriggerCreatesHistoryOnInsertAndStatusUpdate() {
        // 1. Create category
        ReportCategory category = categoryRepository.save(new ReportCategory("Espaces Verts"));

        // 2. Insert report -> Database trigger trg_report_history fires on INSERT
        Report report = new Report();
        report.setTitle("Branche tombée");
        report.setDescription("Arbre sur la chaussée");
        report.setLatitude(48.85);
        report.setLongitude(2.35);
        report.setUserId(200L);
        report.setCategory(category);
        report.setStatus(ReportStatus.SUBMITTED);
        report.setCreatedAt(Instant.now());

        Report saved = reportRepository.saveAndFlush(report);

        // 3. Verify history record created by trigger (old_status NULL, new_status SUBMITTED)
        List<ReportHistory> historyAfterInsert = historyRepository.findByReportIdOrderByChangedAtAsc(saved.getId());
        assertThat(historyAfterInsert).hasSize(1);
        ReportHistory firstEntry = historyAfterInsert.get(0);
        assertThat(firstEntry.getOldStatus()).isNull();
        assertThat(firstEntry.getNewStatus()).isEqualTo(ReportStatus.SUBMITTED);
        assertThat(firstEntry.getChangedAt()).isNotNull();

        // 4. Update status -> Database trigger trg_report_history fires on UPDATE
        saved.setStatus(ReportStatus.VALIDATED);
        reportRepository.saveAndFlush(saved);

        // 5. Verify second history record created by trigger (old_status SUBMITTED, new_status VALIDATED)
        List<ReportHistory> historyAfterUpdate = historyRepository.findByReportIdOrderByChangedAtAsc(saved.getId());
        assertThat(historyAfterUpdate).hasSize(2);

        ReportHistory secondEntry = historyAfterUpdate.get(1);
        assertThat(secondEntry.getOldStatus()).isEqualTo(ReportStatus.SUBMITTED);
        assertThat(secondEntry.getNewStatus()).isEqualTo(ReportStatus.VALIDATED);
        assertThat(secondEntry.getChangedAt()).isAfterOrEqualTo(firstEntry.getChangedAt());
    }
}
