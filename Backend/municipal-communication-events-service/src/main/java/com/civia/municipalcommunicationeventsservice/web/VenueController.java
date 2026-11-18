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

import com.civia.municipalcommunicationeventsservice.service.VenueService;
import com.civia.municipalcommunicationeventsservice.web.dto.VenueRequest;
import com.civia.municipalcommunicationeventsservice.web.dto.VenueResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

	private final VenueService venueService;

	public VenueController(VenueService venueService) {
		this.venueService = venueService;
	}

	@GetMapping
	public List<VenueResponse> findAll() {
		return venueService.findAll().stream().map(VenueResponse::from).toList();
	}

	@GetMapping("/{id}")
	public VenueResponse findById(@PathVariable Long id) {
		return VenueResponse.from(venueService.findById(id));
	}

	@PostMapping
	public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request) {
		VenueResponse created = VenueResponse.from(venueService.create(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public VenueResponse update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
		return VenueResponse.from(venueService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		venueService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
