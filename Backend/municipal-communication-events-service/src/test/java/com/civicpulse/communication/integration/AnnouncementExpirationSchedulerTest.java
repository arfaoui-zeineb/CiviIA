package com.civicpulse.communication.integration;

import com.civicpulse.communication.domain.entity.Announcement;
import com.civicpulse.communication.domain.entity.Category;
import com.civicpulse.communication.domain.enums.AnnouncementPriority;
import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import com.civicpulse.communication.repository.AnnouncementRepository;
import com.civicpulse.communication.repository.CategoryRepository;
import com.civicpulse.communication.scheduler.AnnouncementExpirationScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AnnouncementExpirationSchedulerTest {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AnnouncementExpirationScheduler scheduler;

    @Test
    @DisplayName("Tâche planifiée: Les annonces expirées passent automatiquement à EXPIRED")
    void testScheduledExpiration() {
        Category category = categoryRepository.save(Category.builder()
                .name("Avis publics")
                .active(true)
                .build());

        Announcement pastAnnouncement = Announcement.builder()
                .title("Avis passé")
                .content("Contenu")
                .category(category)
                .departmentId(1L)
                .priority(AnnouncementPriority.NORMAL)
                .status(AnnouncementStatus.PUBLISHED)
                .publishedAt(OffsetDateTime.now().minusDays(5))
                .expiresAt(OffsetDateTime.now().minusMinutes(5)) // Already expired
                .build();

        Announcement futureAnnouncement = Announcement.builder()
                .title("Avis futur")
                .content("Contenu")
                .category(category)
                .departmentId(1L)
                .priority(AnnouncementPriority.NORMAL)
                .status(AnnouncementStatus.PUBLISHED)
                .publishedAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusDays(5)) // Not expired
                .build();

        announcementRepository.save(pastAnnouncement);
        announcementRepository.save(futureAnnouncement);

        // Run scheduler
        scheduler.processExpiredAnnouncements();

        Announcement updatedPast = announcementRepository.findById(pastAnnouncement.getId()).orElseThrow();
        Announcement updatedFuture = announcementRepository.findById(futureAnnouncement.getId()).orElseThrow();

        assertThat(updatedPast.getStatus()).isEqualTo(AnnouncementStatus.EXPIRED);
        assertThat(updatedFuture.getStatus()).isEqualTo(AnnouncementStatus.PUBLISHED);
    }
}

