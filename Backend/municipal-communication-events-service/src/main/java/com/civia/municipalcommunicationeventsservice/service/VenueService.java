package com.civia.municipalcommunicationeventsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Venue;
import com.civia.municipalcommunicationeventsservice.domain.repository.VenueRepository;
import com.civia.municipalcommunicationeventsservice.exception.ResourceNotFoundException;
import com.civia.municipalcommunicationeventsservice.web.dto.VenueRequest;

@Service
@Transactional(readOnly = true)
public class VenueService {

	private final VenueRepository venueRepository;

	public VenueService(VenueRepository venueRepository) {
		this.venueRepository = venueRepository;
	}

	public List<Venue> findAll() {
		return venueRepository.findAll();
	}

	public Venue findById(Long id) {
		return venueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Venue " + id + " not found"));
	}

	@Transactional
	public Venue create(VenueRequest request) {
		Venue venue = Venue.builder()
				.name(request.name())
				.address(request.address())
				.latitude(request.latitude())
				.longitude(request.longitude())
				.capacity(request.capacity())
				.build();
		return venueRepository.save(venue);
	}

	@Transactional
	public Venue update(Long id, VenueRequest request) {
		Venue venue = findById(id);
		venue.setName(request.name());
		venue.setAddress(request.address());
		venue.setLatitude(request.latitude());
		venue.setLongitude(request.longitude());
		venue.setCapacity(request.capacity());
		return venueRepository.save(venue);
	}

	@Transactional
	public void delete(Long id) {
		venueRepository.delete(findById(id));
	}
}
