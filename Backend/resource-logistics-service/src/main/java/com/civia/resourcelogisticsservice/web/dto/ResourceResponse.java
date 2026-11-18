package com.civia.resourcelogisticsservice.web.dto;

import com.civia.resourcelogisticsservice.domain.model.Resource;
import com.civia.resourcelogisticsservice.domain.model.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.model.ResourceType;

public record ResourceResponse(
		Long id,
		String name,
		ResourceType type,
		ResourceStatus status,
		String location,
		boolean available) {

	public static ResourceResponse from(Resource resource) {
		return new ResourceResponse(resource.getId(), resource.getName(), resource.getType(), resource.getStatus(),
				resource.getLocation(), resource.isAvailable());
	}
}
