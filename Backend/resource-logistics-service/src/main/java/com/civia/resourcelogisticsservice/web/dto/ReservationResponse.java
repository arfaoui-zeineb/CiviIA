package com.civia.resourcelogisticsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.resourcelogisticsservice.domain.model.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.model.ResourceReservation;

public record ReservationResponse(
		Long id,
		Long interventionId,
		Long resourceId,
		Long teamId,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		ReservationStatus status) {

	public static ReservationResponse from(ResourceReservation reservation) {
		return new ReservationResponse(reservation.getId(), reservation.getInterventionId(),
				reservation.getResourceId(), reservation.getTeamId(), reservation.getStartDateTime(),
				reservation.getEndDateTime(), reservation.getStatus());
	}
}
