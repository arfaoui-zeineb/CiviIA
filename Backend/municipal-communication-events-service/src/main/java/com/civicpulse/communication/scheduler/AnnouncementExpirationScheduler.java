package com.civicpulse.communication.scheduler;

import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import com.civicpulse.communication.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnnouncementExpirationScheduler {

    private final AnnouncementRepository announcementRepository;

    /**
     * Rule 5: Annonce dont expiresAt est dépassée -> status EXPIRED via tâche planifiée (@Scheduled).
     * Runs periodically according to app.scheduling.announcement-expiration-cron (e.g. every minute).
     */
    @Scheduled(cron = "${app.scheduling.announcement-expiration-cron:0 * * * * *}")
    @Transactional
    public void processExpiredAnnouncements() {
        OffsetDateTime now = OffsetDateTime.now();
        int expiredCount = announcementRepository.markExpiredAnnouncements(now, AnnouncementStatus.EXPIRED);
        if (expiredCount > 0) {
            log.info("AnnouncementExpirationScheduler: Marked {} announcements as EXPIRED at {}", expiredCount, now);
        }
    }
}

