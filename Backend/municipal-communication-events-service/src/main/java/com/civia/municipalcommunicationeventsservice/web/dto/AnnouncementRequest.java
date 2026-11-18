package com.civia.municipalcommunicationeventsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementPriority;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementStatus;
import jakarta.validation.constraints.NotBlank;

public record AnnouncementRequest(
		Long categoryId,
		Long departmentId,
		Long eventId,
		@NotBlank String title,
		String content,
		AnnouncementStatus status,
		AnnouncementPriority priority,
		LocalDateTime publishedAt,
		LocalDateTime expiresAt) {
}
