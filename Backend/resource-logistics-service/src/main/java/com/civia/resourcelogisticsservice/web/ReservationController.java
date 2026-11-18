package com.civia.resourcelogisticsservice.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.civia.resourcelogisticsservice.domain.model.ResourceReservation;
import com.civia.resourcelogisticsservice.service.ResourceReservationService;
import com.civia.resourcelogisticsservice.web.dto.ReservationRequest;
import com.civia.resourcelogisticsservice.web.dto.ReservationResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ResourceReservationService reservationService;

	public ReservationController(ResourceReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping
	public List<ReservationResponse> findAll(@RequestParam(required = false) Long interventionId) {
		List<ResourceReservation> reservations =
				interventionId != null
						? reservationService.findByInterventionId(interventionId)
						: reservationService.findAll();
		return reservations.stream().map(ReservationResponse::from).toList();
	}

	@GetMapping("/{id}")
	public ReservationResponse findById(@PathVariable Long id) {
		return ReservationResponse.from(reservationService.findById(id));
	}

	@PostMapping
	public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
		ReservationResponse created = ReservationResponse.from(reservationService.create(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PostMapping("/{id}/cancel")
	public ReservationResponse cancel(@PathVariable Long id) {
		return ReservationResponse.from(reservationService.cancel(id));
	}
}
