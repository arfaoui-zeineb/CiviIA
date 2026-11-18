package com.civia.municipalcommunicationeventsservice.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.civia.municipalcommunicationeventsservice.service.EventRegistrationService;
import com.civia.municipalcommunicationeventsservice.service.EventService;
import com.civia.municipalcommunicationeventsservice.web.dto.EventRequest;
import com.civia.municipalcommunicationeventsservice.web.dto.EventResponse;
import com.civia.municipalcommunicationeventsservice.web.dto.RegistrationRequest;
import com.civia.municipalcommunicationeventsservice.web.dto.RegistrationResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

	private final EventService eventService;
	private final EventRegistrationService registrationService;

	public EventController(EventService eventService, EventRegistrationService registrationService) {
		this.eventService = eventService;
		this.registrationService = registrationService;
	}

	@GetMapping
	public List<EventResponse> findAll() {
		return eventService.findAll().stream().map(EventResponse::from).toList();
	}

	@GetMapping("/{id}")
	public EventResponse findById(@PathVariable Long id) {
		return EventResponse.from(eventService.findById(id));
	}

	@PostMapping
	public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
		EventResponse created = EventResponse.from(eventService.create(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
		return EventResponse.from(eventService.update(id, request));
	}

	@PostMapping("/{id}/open")
	public EventResponse open(@PathVariable Long id) {
		return EventResponse.from(eventService.open(id));
	}

	/** DELETE annule l'evenement (§ 4.6). */
	@DeleteMapping("/{id}")
	public EventResponse cancel(@PathVariable Long id) {
		return EventResponse.from(eventService.cancel(id));
	}

	@GetMapping("/{id}/registrations")
	public List<RegistrationResponse> findRegistrations(@PathVariable Long id) {
		return registrationService.findByEventId(id).stream().map(RegistrationResponse::from).toList();
	}

	@PostMapping("/{id}/registrations")
	public ResponseEntity<RegistrationResponse> register(@PathVariable Long id,
			@Valid @RequestBody RegistrationRequest request) {
		RegistrationResponse created = RegistrationResponse.from(
				registrationService.register(id, request.userId()));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}
}
