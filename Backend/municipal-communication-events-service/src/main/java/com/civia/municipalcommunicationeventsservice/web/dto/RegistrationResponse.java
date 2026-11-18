package com.civia.municipalcommunicationeventsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.EventRegistration;
import com.civia.municipalcommunicationeventsservice.domain.model.RegistrationStatus;

public record RegistrationResponse(
		Long id,
		Long eventId,
		Long userId,
		RegistrationStatus status,
		LocalDateTime registeredAt,
		LocalDateTime cancelledAt) {

	public static RegistrationResponse from(EventRegistration registration) {
		return new RegistrationResponse(registration.getId(), registration.getEventId(), registration.getUserId(),
				registration.getStatus(), registration.getRegisteredAt(), registration.getCancelledAt());
	}
}
