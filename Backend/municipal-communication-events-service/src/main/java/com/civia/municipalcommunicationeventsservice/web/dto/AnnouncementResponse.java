package com.civia.municipalcommunicationeventsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.Announcement;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementPriority;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementStatus;

public record AnnouncementResponse(
		Long id,
		Long categoryId,
		Long departmentId,
		Long eventId,
		String title,
		String content,
		AnnouncementStatus status,
		AnnouncementPriority priority,
		LocalDateTime publishedAt,
		LocalDateTime expiresAt) {

	public static AnnouncementResponse from(Announcement announcement) {
		return new AnnouncementResponse(announcement.getId(), announcement.getCategoryId(),
				announcement.getDepartmentId(), announcement.getEventId(), announcement.getTitle(),
				announcement.getContent(), announcement.getStatus(), announcement.getPriority(),
				announcement.getPublishedAt(), announcement.getExpiresAt());
	}
}
