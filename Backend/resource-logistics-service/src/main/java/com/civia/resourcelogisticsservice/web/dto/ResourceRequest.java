package com.civia.resourcelogisticsservice.web.dto;

import com.civia.resourcelogisticsservice.domain.model.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.model.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResourceRequest(
		@NotBlank String name,
		@NotNull ResourceType type,
		ResourceStatus status,
		String location) {
}
