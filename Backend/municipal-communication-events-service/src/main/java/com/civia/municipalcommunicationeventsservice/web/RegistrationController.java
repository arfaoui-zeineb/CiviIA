package com.civia.municipalcommunicationeventsservice.web;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.civia.municipalcommunicationeventsservice.service.EventRegistrationService;
import com.civia.municipalcommunicationeventsservice.web.dto.RegistrationResponse;

@RestController
@RequestMapping("/api/event-registrations")
public class RegistrationController {

	private final EventRegistrationService registrationService;

	public RegistrationController(EventRegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@GetMapping("/{id}")
	public RegistrationResponse findById(@PathVariable Long id) {
		return RegistrationResponse.from(registrationService.findById(id));
	}

	@DeleteMapping("/{id}")
	public RegistrationResponse cancel(@PathVariable Long id) {
		return RegistrationResponse.from(registrationService.cancel(id));
	}
}
