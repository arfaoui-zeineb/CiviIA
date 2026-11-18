package com.civia.municipalcommunicationeventsservice.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VenueRequest(
		@NotBlank String name,
		String address,
		Double latitude,
		Double longitude,
		@NotNull @Min(1) Integer capacity) {
}
