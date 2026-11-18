package com.civia.municipalcommunicationeventsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequest(
		Long categoryId,
		Long venueId,
		Long departmentId,
		@NotBlank String title,
		String description,
		@NotNull LocalDateTime startDate,
		@NotNull LocalDateTime endDate,
		@NotNull @Min(1) Integer capacity,
		EventStatus status) {
}
