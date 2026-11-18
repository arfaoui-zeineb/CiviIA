package com.civia.resourcelogisticsservice.web.dto;

import com.civia.resourcelogisticsservice.domain.model.Team;
import com.civia.resourcelogisticsservice.domain.model.TeamStatus;

public record TeamResponse(
		Long id,
		String name,
		String specialty,
		String zone,
		TeamStatus status,
		boolean available) {

	public static TeamResponse from(Team team) {
		return new TeamResponse(team.getId(), team.getName(), team.getSpecialty(), team.getZone(),
				team.getStatus(), team.isAvailable());
	}
}
