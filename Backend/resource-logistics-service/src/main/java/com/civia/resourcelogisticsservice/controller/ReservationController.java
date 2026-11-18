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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Reservation")
@Tag(name = "Reservations", description = "Gestion des reservations")
public class ReservationController {

	private final IResourceReservationService reservationService;

	@PostMapping("/addReservation")
	@Operation(summary = "addReservation", description = "Creer et confirmer une reservation")
	public ResourceReservation addReservation(@RequestBody ResourceReservation reservation) {
		return reservationService.addReservation(reservation);
	}

	@GetMapping("/getAllReservations")
	@Operation(summary = "getAllReservations", description = "Recuperer les reservations (filtre interventionId optionnel)")
	public List<ResourceReservation> getAllReservations(@RequestParam(required = false) Long interventionId) {
		return interventionId != null
				? reservationService.getReservationsByIntervention(interventionId)
				: reservationService.getAllReservations();
	}

	@GetMapping("/getReservation/{idReservation}")
	@Operation(summary = "getReservation", description = "Recuperer une reservation par identifiant")
	public ResourceReservation getReservation(
			@Parameter(name = "idReservation", description = "idReservation", required = true)
			@PathVariable("idReservation") Long idReservation) {
		return reservationService.getReservation(idReservation);
	}

	@PutMapping("/updateReservation")
	@Operation(summary = "updateReservation", description = "Remplacer une reservation active")
	public ResourceReservation updateReservation(@RequestBody ResourceReservation reservation) {
		return reservationService.updateReservation(reservation);
	}

	@DeleteMapping("/deleteReservation/{idReservation}")
	@Operation(summary = "deleteReservation", description = "Supprimer une reservation et liberer la ressource")
	public void deleteReservation(
			@Parameter(name = "idReservation", description = "idReservation", required = true)
			@PathVariable("idReservation") Long idReservation) {
		reservationService.deleteReservation(idReservation);
	}

	@PostMapping("/cancelReservation/{idReservation}")
	@Operation(summary = "cancelReservation", description = "Annuler une reservation")
	public ResourceReservation cancelReservation(
			@Parameter(name = "idReservation", description = "idReservation", required = true)
			@PathVariable("idReservation") Long idReservation) {
		return reservationService.cancelReservation(idReservation);
	}
}
