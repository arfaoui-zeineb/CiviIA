package com.civia.municipalcommunicationeventsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Event;
import com.civia.municipalcommunicationeventsservice.domain.model.EventRegistration;
import com.civia.municipalcommunicationeventsservice.domain.model.EventStatus;
import com.civia.municipalcommunicationeventsservice.domain.model.RegistrationStatus;
import com.civia.municipalcommunicationeventsservice.domain.repository.EventRegistrationRepository;
import com.civia.municipalcommunicationeventsservice.domain.repository.EventRepository;
import com.civia.municipalcommunicationeventsservice.exception.BusinessRuleException;
import com.civia.municipalcommunicationeventsservice.exception.ConflictException;
import com.civia.municipalcommunicationeventsservice.exception.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class EventRegistrationService {

	private final EventRegistrationRepository registrationRepository;
	private final EventRepository eventRepository;

	public EventRegistrationService(EventRegistrationRepository registrationRepository, EventRepository eventRepository) {
		this.registrationRepository = registrationRepository;
		this.eventRepository = eventRepository;
	}

	public List<EventRegistration> findByEventId(Long eventId) {
		return registrationRepository.findByEventId(eventId);
	}

	public EventRegistration findById(Long id) {
		return registrationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Registration " + id + " not found"));
	}

	/**
	 * Une inscription est refuse si la capacite est atteinte. Le compteur est
	 * incremente dans la meme transaction que l'ecriture ; la version optimiste
	 * de Event (§ 4.6) garantit l'atomicite en cas d'inscriptions simultanees.
	 */
	@Transactional
	public EventRegistration register(Long eventId, Long userId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new ResourceNotFoundException("Event " + eventId + " not found"));
		if (event.getStatus() != EventStatus.OPEN) {
			throw new BusinessRuleException("Event " + eventId + " is not open for registration");
		}
		if (event.isFull()) {
			throw new ConflictException("Event " + eventId + " has reached its capacity");
		}
		if (registrationRepository.existsByEventIdAndUserIdAndStatus(
				eventId, userId, RegistrationStatus.REGISTERED)) {
			throw new ConflictException("User " + userId + " already has an active registration for event " + eventId);
		}

		event.setRegisteredCount(event.getRegisteredCount() + 1);
		if (event.isFull()) {
			event.setStatus(EventStatus.FULL);
		}
		eventRepository.save(event);

		EventRegistration registration = EventRegistration.builder()
				.eventId(eventId)
				.userId(userId)
				.status(RegistrationStatus.REGISTERED)
				.build();
		return registrationRepository.save(registration);
	}

	/** Une annulation renseigne cancelledAt et decremente registeredCount. */
	@Transactional
	public EventRegistration cancel(Long id) {
		EventRegistration registration = findById(id);
		registration.cancel();

		Event event = eventRepository.findById(registration.getEventId())
				.orElseThrow(() -> new ResourceNotFoundException("Event " + registration.getEventId() + " not found"));
		if (event.getRegisteredCount() > 0) {
			event.setRegisteredCount(event.getRegisteredCount() - 1);
		}
		if (event.getStatus() == EventStatus.FULL) {
			event.setStatus(EventStatus.OPEN);
		}
		eventRepository.save(event);
		return registrationRepository.save(registration);
	}
}
