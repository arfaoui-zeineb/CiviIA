package com.civia.resourcelogisticsservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.service.IResourceReservationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

	private final IResourceReservationService reservationService;

	@PostMapping
	public ResourceReservation addReservation(@RequestBody ResourceReservation reservation) {
		return reservationService.addReservation(reservation);
	}

	@GetMapping
	public List<ResourceReservation> retrieveAllReservations(@RequestParam(required = false) Long interventionId) {
		return interventionId != null
				? reservationService.retrieveReservationsByIntervention(interventionId)
				: reservationService.retrieveAllReservations();
	}

	@GetMapping("/{id}")
	public ResourceReservation retrieveReservation(@PathVariable Long id) {
		return reservationService.retrieveReservation(id);
	}

	@PutMapping("/{id}")
	public ResourceReservation updateReservation(@PathVariable Long id,
			@RequestBody ResourceReservation reservation) {
		reservation.setId(id);
		return reservationService.updateReservation(reservation);
	}

	@DeleteMapping("/{id}")
	public void removeReservation(@PathVariable Long id) {
		reservationService.removeReservation(id);
	}

	@PostMapping("/{id}/cancel")
	public ResourceReservation cancelReservation(@PathVariable Long id) {
		return reservationService.cancelReservation(id);
	}
}
