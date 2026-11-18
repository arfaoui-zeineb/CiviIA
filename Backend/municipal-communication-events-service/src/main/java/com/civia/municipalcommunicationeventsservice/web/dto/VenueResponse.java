package com.civia.municipalcommunicationeventsservice.web.dto;

import com.civia.municipalcommunicationeventsservice.domain.model.Venue;

public record VenueResponse(
		Long id,
		String name,
		String address,
		Double latitude,
		Double longitude,
		int capacity) {

	public static VenueResponse from(Venue venue) {
		return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(), venue.getLatitude(),
				venue.getLongitude(), venue.getCapacity());
	}
}
