package com.civia.municipalcommunicationeventsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Event;
import com.civia.municipalcommunicationeventsservice.domain.model.EventStatus;
import com.civia.municipalcommunicationeventsservice.domain.model.Venue;
import com.civia.municipalcommunicationeventsservice.domain.repository.EventRepository;
import com.civia.municipalcommunicationeventsservice.domain.repository.VenueRepository;
import com.civia.municipalcommunicationeventsservice.exception.ResourceNotFoundException;
import com.civia.municipalcommunicationeventsservice.web.dto.EventRequest;

@Service
@Transactional(readOnly = true)
public class EventService {

	private final EventRepository eventRepository;
	private final VenueRepository venueRepository;

	public EventService(EventRepository eventRepository, VenueRepository venueRepository) {
		this.eventRepository = eventRepository;
		this.venueRepository = venueRepository;
	}

	public List<Event> findAll() {
		return eventRepository.findAll();
	}

	public Event findById(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event " + id + " not found"));
	}

	@Transactional
	public Event create(EventRequest request) {
		Event event = Event.builder()
				.categoryId(request.categoryId())
				.venueId(request.venueId())
				.departmentId(request.departmentId())
				.title(request.title())
				.description(request.description())
				.startDate(request.startDate())
				.endDate(request.endDate())
				.capacity(request.capacity())
				.registeredCount(0)
				.status(request.status() != null ? request.status() : EventStatus.DRAFT)
				.build();
		validate(event);
		return eventRepository.save(event);
	}

	@Transactional
	public Event update(Long id, EventRequest request) {
		Event event = findById(id);
		event.setCategoryId(request.categoryId());
		event.setVenueId(request.venueId());
		event.setDepartmentId(request.departmentId());
		event.setTitle(request.title());
		event.setDescription(request.description());
		event.setStartDate(request.startDate());
		event.setEndDate(request.endDate());
		event.setCapacity(request.capacity());
		event.setStatus(request.status() != null ? request.status() : event.getStatus());
		validate(event);
		return eventRepository.save(event);
	}

	@Transactional
	public Event open(Long id) {
		Event event = findById(id);
		event.open();
		return eventRepository.save(event);
	}

	@Transactional
	public Event cancel(Long id) {
		Event event = findById(id);
		event.cancel();
		return eventRepository.save(event);
	}

	@Transactional
	public void delete(Long id) {
		eventRepository.delete(findById(id));
	}

	private void validate(Event event) {
		if (!event.isValidPeriod()) {
			throw new IllegalArgumentException("endDate must be after startDate");
		}
		Venue venue = event.getVenueId() != null ? venueRepository.findById(event.getVenueId()).orElseThrow(
				() -> new ResourceNotFoundException("Venue " + event.getVenueId() + " not found")) : null;
		event.validateCapacityAgainstVenue(venue);
	}
}
