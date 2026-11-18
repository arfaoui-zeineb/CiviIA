package com.civia.municipalcommunicationeventsservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Announcement;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementStatus;
import com.civia.municipalcommunicationeventsservice.domain.repository.AnnouncementRepository;

/** Tache planifiee : une annonce dont expiresAt est depassee passe a EXPIRE (§ 4.6). */
@Service
@Transactional(readOnly = true)
public class AnnouncementExpirationService {

	private final AnnouncementRepository announcementRepository;

	public AnnouncementExpirationService(AnnouncementRepository announcementRepository) {
		this.announcementRepository = announcementRepository;
	}

	@Scheduled(fixedDelayString = "PT1H")
	@Transactional
	public void expireOutdatedAnnouncements() {
		LocalDateTime now = LocalDateTime.now();
		List<Announcement> toExpire = new ArrayList<>();
		toExpire.addAll(announcementRepository.findByStatusAndExpiresAtBefore(AnnouncementStatus.DRAFT, now));
		toExpire.addAll(announcementRepository.findByStatusAndExpiresAtBefore(AnnouncementStatus.PUBLISHED, now));
		toExpire.forEach(Announcement::expire);
		announcementRepository.saveAll(toExpire);
	}
}
