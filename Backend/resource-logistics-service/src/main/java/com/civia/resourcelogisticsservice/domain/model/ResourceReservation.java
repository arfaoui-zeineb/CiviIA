package com.civia.resourcelogisticsservice.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import com.civia.resourcelogisticsservice.exception.BusinessRuleException;
import com.civia.resourcelogisticsservice.exception.ConflictException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reservation d'une ressource (et optionally d'une equipe) pour une intervention.
 * Les champs resourceId (obligatoire) et teamId (optionnel) correspondent a la
 * recommandation § 8.3.3 du dossier technique.
 */
@Entity
@Table(name = "resource_reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceReservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false)
	private Long interventionId;

	@NotNull
	@Column(nullable = false)
	private Long resourceId;

	private Long teamId;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime startDateTime;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime endDateTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private ReservationStatus status = ReservationStatus.PENDING;

	@AssertTrue(message = "startDateTime must be before endDateTime")
	@JsonIgnore
	public boolean isValidPeriod() {
		return startDateTime != null && endDateTime != null && startDateTime.isBefore(endDateTime);
	}

	/** PENDING et CONFIRMED sont les statuts actifs d'une reservation. */
	public boolean isActive() {
		return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
	}

	/** Deux intervalles qui se touchent exactement ne se chevauchent pas. */
	public boolean overlaps(ResourceReservation other) {
		return startDateTime.isBefore(other.getEndDateTime())
				&& other.getStartDateTime().isBefore(endDateTime);
	}

	/** checkAvailability() : refuse un chevauchement avec une reservation active. */
	public void checkAvailability(List<ResourceReservation> existingReservations) {
		boolean conflict = existingReservations.stream()
				.filter(reservation -> reservation.getId() == null || !reservation.getId().equals(this.id))
				.filter(ResourceReservation::isActive)
				.anyMatch(this::overlaps);
		if (conflict) {
			throw new ConflictException("Resource " + resourceId + " is already reserved over the requested period");
		}
	}

	/** reserve() : passe en CONFIRMED et met la ressource en RESERVED. */
	public void reserve(Resource resource) {
		if (!resource.isAvailable()) {
			throw new BusinessRuleException("Resource " + resource.getId() + " is not available");
		}
		if (status != ReservationStatus.PENDING) {
			throw new BusinessRuleException("Only a PENDING reservation can be reserved");
		}
		this.status = ReservationStatus.CONFIRMED;
		resource.changeStatus(ResourceStatus.RESERVED);
	}

	/** cancel() : libere la reservation (la ressource est liberée par le service). */
	public void cancel() {
		if (status == ReservationStatus.CANCELLED || status == ReservationStatus.COMPLETED) {
			throw new BusinessRuleException("Reservation " + id + " is already closed");
		}
		this.status = ReservationStatus.CANCELLED;
	}
}
