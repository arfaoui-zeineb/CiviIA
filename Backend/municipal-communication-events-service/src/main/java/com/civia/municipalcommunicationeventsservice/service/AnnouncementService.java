package com.civia.municipalcommunicationeventsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Announcement;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementPriority;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementStatus;
import com.civia.municipalcommunicationeventsservice.domain.repository.AnnouncementRepository;
import com.civia.municipalcommunicationeventsservice.exception.ResourceNotFoundException;
import com.civia.municipalcommunicationeventsservice.web.dto.AnnouncementRequest;

@Service
@Transactional(readOnly = true)
public class AnnouncementService {

	private final AnnouncementRepository announcementRepository;

	public AnnouncementService(AnnouncementRepository announcementRepository) {
		this.announcementRepository = announcementRepository;
	}

	public List<Announcement> findAll() {
		return announcementRepository.findAll();
	}

	public Announcement findById(Long id) {
		return announcementRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Announcement " + id + " not found"));
	}

	@Transactional
	public Announcement create(AnnouncementRequest request) {
		Announcement announcement = Announcement.builder()
				.categoryId(request.categoryId())
				.departmentId(request.departmentId())
				.eventId(request.eventId())
				.title(request.title())
				.content(request.content())
				.status(request.status() != null ? request.status() : AnnouncementStatus.DRAFT)
				.priority(request.priority() != null ? request.priority() : AnnouncementPriority.NORMAL)
				.publishedAt(request.publishedAt())
				.expiresAt(request.expiresAt())
				.build();
		return announcementRepository.save(announcement);
	}

	@Transactional
	public Announcement update(Long id, AnnouncementRequest request) {
		Announcement announcement = findById(id);
		announcement.setCategoryId(request.categoryId());
		announcement.setDepartmentId(request.departmentId());
		announcement.setEventId(request.eventId());
		announcement.setTitle(request.title());
		announcement.setContent(request.content());
		announcement.setStatus(request.status() != null ? request.status() : announcement.getStatus());
		announcement.setPriority(request.priority() != null ? request.priority() : announcement.getPriority());
		announcement.setPublishedAt(request.publishedAt());
		announcement.setExpiresAt(request.expiresAt());
		return announcementRepository.save(announcement);
	}

	@Transactional
	public Announcement publish(Long id) {
		Announcement announcement = findById(id);
		announcement.publish();
		return announcementRepository.save(announcement);
	}

	@Transactional
	public Announcement archive(Long id) {
		Announcement announcement = findById(id);
		announcement.archive();
		return announcementRepository.save(announcement);
	}

	@Transactional
	public void delete(Long id) {
		announcementRepository.delete(findById(id));
	}
}
