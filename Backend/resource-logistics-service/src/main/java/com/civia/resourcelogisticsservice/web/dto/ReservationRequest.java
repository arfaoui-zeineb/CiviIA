package com.civia.resourcelogisticsservice.web.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ReservationRequest(
		@NotNull Long interventionId,
		@NotNull Long resourceId,
		Long teamId,
		@NotNull LocalDateTime startDateTime,
		@NotNull LocalDateTime endDateTime) {
}
