package com.civia.resourcelogisticsservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.resourcelogisticsservice.domain.model.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.model.Resource;
import com.civia.resourcelogisticsservice.domain.model.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.model.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.model.Team;
import com.civia.resourcelogisticsservice.domain.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.domain.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.domain.repository.TeamRepository;
import com.civia.resourcelogisticsservice.exception.BusinessRuleException;
import com.civia.resourcelogisticsservice.exception.ConflictException;
import com.civia.resourcelogisticsservice.exception.ResourceNotFoundException;
import com.civia.resourcelogisticsservice.web.dto.ReservationRequest;

@Service
@Transactional(readOnly = true)
public class ResourceReservationService {

	private static final List<ReservationStatus> ACTIVE_STATUSES =
			List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

	private final ResourceReservationRepository reservationRepository;
	private final ResourceRepository resourceRepository;
	private final TeamRepository teamRepository;

	public ResourceReservationService(ResourceReservationRepository reservationRepository,
			ResourceRepository resourceRepository, TeamRepository teamRepository) {
		this.reservationRepository = reservationRepository;
		this.resourceRepository = resourceRepository;
		this.teamRepository = teamRepository;
	}

	public List<ResourceReservation> findAll() {
		return reservationRepository.findAll();
	}

	public List<ResourceReservation> findByInterventionId(Long interventionId) {
		return reservationRepository.findByInterventionId(interventionId);
	}

	public ResourceReservation findById(Long id) {
		return reservationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation " + id + " not found"));
	}

	/**
	 * reserve() : verifie la disponibilite de la ressource et l'absence de
	 * chevauchement, puis confirme la reservation.
	 */
	@Transactional
	public ResourceReservation create(ReservationRequest request) {
		validatePeriod(request.startDateTime(), request.endDateTime());

		Resource resource = resourceRepository.findById(request.resourceId())
				.orElseThrow(() -> new ResourceNotFoundException("Resource " + request.resourceId() + " not found"));

		if (request.teamId() != null) {
			checkTeamAvailability(request.teamId(), request.startDateTime(), request.endDateTime());
		}

		ResourceReservation reservation = ResourceReservation.builder()
				.interventionId(request.interventionId())
				.resourceId(request.resourceId())
				.teamId(request.teamId())
				.startDateTime(request.startDateTime())
				.endDateTime(request.endDateTime())
				.status(ReservationStatus.PENDING)
				.build();

		reservation.checkAvailability(
				reservationRepository.findByResourceIdAndStatusIn(request.resourceId(), ACTIVE_STATUSES));
		reservation.reserve(resource);

		ResourceReservation saved = reservationRepository.save(reservation);
		resourceRepository.save(resource);
		return saved;
	}

	/** cancel() : annule la reservation et libere la ressource si elle n'est plus retenue. */
	@Transactional
	public ResourceReservation cancel(Long id) {
		ResourceReservation reservation = findById(id);
		reservation.cancel();

		Resource resource = resourceRepository.findById(reservation.getResourceId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Resource " + reservation.getResourceId() + " not found"));

		boolean stillReserved = reservationRepository
				.findByResourceIdAndStatusIn(resource.getId(), ACTIVE_STATUSES).stream()
				.anyMatch(other -> !other.getId().equals(id) && other.isActive());
		if (!stillReserved) {
			resource.changeStatus(ResourceStatus.AVAILABLE);
		}

		resourceRepository.save(resource);
		return reservationRepository.save(reservation);
	}

	private void checkTeamAvailability(Long teamId, LocalDateTime start, LocalDateTime end) {
		Team team = teamRepository.findById(teamId)
				.orElseThrow(() -> new ResourceNotFoundException("Team " + teamId + " not found"));
		if (!team.isAvailable()) {
			throw new BusinessRuleException("Team " + teamId + " is not available");
		}
		ResourceReservation probe = ResourceReservation.builder()
				.teamId(teamId)
				.startDateTime(start)
				.endDateTime(end)
				.build();
		if (reservationRepository.findByTeamIdAndStatusIn(teamId, ACTIVE_STATUSES).stream()
				.anyMatch(probe::overlaps)) {
			throw new ConflictException("Team " + teamId + " is already reserved over the requested period");
		}
	}

	private void validatePeriod(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null || !start.isBefore(end)) {
			throw new IllegalArgumentException("startDateTime must be before endDateTime");
		}
	}
}
