package com.civia.municipalcommunicationeventsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.Event;
import com.civia.municipalcommunicationeventsservice.domain.model.EventStatus;

public record EventResponse(
		Long id,
		Long categoryId,
		Long venueId,
		Long departmentId,
		String title,
		String description,
		LocalDateTime startDate,
		LocalDateTime endDate,
		int capacity,
		int registeredCount,
		EventStatus status) {

	public static EventResponse from(Event event) {
		return new EventResponse(event.getId(), event.getCategoryId(), event.getVenueId(), event.getDepartmentId(),
				event.getTitle(), event.getDescription(), event.getStartDate(), event.getEndDate(),
				event.getCapacity(), event.getRegisteredCount(), event.getStatus());
	}
}
