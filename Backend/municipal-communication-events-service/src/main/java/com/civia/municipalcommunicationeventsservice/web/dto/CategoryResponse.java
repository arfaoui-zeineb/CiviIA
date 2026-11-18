package com.civia.municipalcommunicationeventsservice.web.dto;

import com.civia.municipalcommunicationeventsservice.domain.model.Category;

public record CategoryResponse(Long id, String name, String description, boolean active) {

	public static CategoryResponse from(Category category) {
		return new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.isActive());
	}
}
