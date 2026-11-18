package com.civia.resourcelogisticsservice.web.dto;

import com.civia.resourcelogisticsservice.domain.model.TeamStatus;
import jakarta.validation.constraints.NotBlank;

public record TeamRequest(
		@NotBlank String name,
		String specialty,
		String zone,
		TeamStatus status) {
}
