package com.civia.resourcelogisticsservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.exception.BusinessRuleException;
import com.civia.resourcelogisticsservice.exception.ConflictException;
import com.civia.resourcelogisticsservice.exception.ResourceNotFoundException;
import com.civia.resourcelogisticsservice.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceReservationService implements IResourceReservationService {

	private static final List<ReservationStatus> ACTIVE_STATUSES =
			List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

	private final ResourceReservationRepository reservationRepository;
	private final ResourceRepository resourceRepository;
	private final TeamRepository teamRepository;

	@Override
	public List<ResourceReservation> retrieveAllReservations() {
		return reservationRepository.findAll();
	}

	@Override
	public List<ResourceReservation> retrieveReservationsByIntervention(Long interventionId) {
		return reservationRepository.findByInterventionId(interventionId);
	}

	@Override
	public ResourceReservation retrieveReservation(Long idReservation) {
		return reservationRepository.findById(idReservation)
				.orElseThrow(() -> new ResourceNotFoundException("Reservation " + idReservation + " not found"));
	}

	/** addReservation : verifie les disponibilites puis confirme la reservation. */
	@Override
	@Transactional
	public ResourceReservation addReservation(ResourceReservation reservation) {
		validatePeriod(reservation.getStartDateTime(), reservation.getEndDateTime());
		if (reservation.getStatus() == null) {
			reservation.setStatus(ReservationStatus.PENDING);
		}

		Resource resource = resourceRepository.findById(reservation.getResourceId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Resource " + reservation.getResourceId() + " not found"));

		if (reservation.getTeamId() != null) {
			checkTeamAvailability(reservation.getTeamId(), reservation.getStartDateTime(),
					reservation.getEndDateTime(), null);
		}

		reservation.checkAvailability(reservationRepository
				.findByResourceIdAndStatusIn(reservation.getResourceId(), ACTIVE_STATUSES));

		if (reservation.getStatus() == ReservationStatus.PENDING) {
			reservation.reserve(resource);
		} else {
			resource.changeStatus(ResourceStatus.RESERVED);
		}

		ResourceReservation saved = reservationRepository.save(reservation);
		resourceRepository.save(resource);
		return saved;
	}

	/** updateReservation : remplace une reservation active en excluant la reservation elle-meme. */
	@Override
	@Transactional
	public ResourceReservation updateReservation(ResourceReservation reservation) {
		ResourceReservation existing = retrieveReservation(reservation.getId());
		if (!existing.isActive()) {
			throw new BusinessRuleException("Only an active reservation can be updated");
		}
		validatePeriod(reservation.getStartDateTime(), reservation.getEndDateTime());

		Resource resource = resourceRepository.findById(reservation.getResourceId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Resource " + reservation.getResourceId() + " not found"));

		if (reservation.getTeamId() != null) {
			checkTeamAvailability(reservation.getTeamId(), reservation.getStartDateTime(),
					reservation.getEndDateTime(), existing.getId());
		}

		ResourceReservation probe = ResourceReservation.builder()
				.id(existing.getId())
				.resourceId(reservation.getResourceId())
				.startDateTime(reservation.getStartDateTime())
				.endDateTime(reservation.getEndDateTime())
				.build();
		probe.checkAvailability(reservationRepository
				.findByResourceIdAndStatusIn(reservation.getResourceId(), ACTIVE_STATUSES));

		boolean resourceChanged = !existing.getResourceId().equals(reservation.getResourceId());
		if (resourceChanged) {
			releaseResourceIfUnused(existing.getResourceId(), existing.getId());
		}

		existing.setInterventionId(reservation.getInterventionId());
		existing.setResourceId(reservation.getResourceId());
		existing.setTeamId(reservation.getTeamId());
		existing.setStartDateTime(reservation.getStartDateTime());
		existing.setEndDateTime(reservation.getEndDateTime());

		resource.changeStatus(ResourceStatus.RESERVED);
		resourceRepository.save(resource);
		return reservationRepository.save(existing);
	}

	/** cancelReservation : annule et libere la ressource si elle n'est plus retenue. */
	@Override
	@Transactional
	public ResourceReservation cancelReservation(Long idReservation) {
		ResourceReservation reservation = retrieveReservation(idReservation);
		reservation.cancel();
		releaseResourceIfUnused(reservation.getResourceId(), idReservation);
		return reservationRepository.save(reservation);
	}

	/** removeReservation : supprime et libere la ressource si elle n'est plus retenue (DELETE). */
	@Override
	@Transactional
	public void removeReservation(Long idReservation) {
		ResourceReservation reservation = retrieveReservation(idReservation);
		if (reservation.isActive()) {
			releaseResourceIfUnused(reservation.getResourceId(), idReservation);
		}
		reservationRepository.deleteById(idReservation);
	}

	private void releaseResourceIfUnused(Long resourceId, Long excludedReservationId) {
		resourceRepository.findById(resourceId).ifPresent(resource -> {
			boolean stillReserved = reservationRepository
					.findByResourceIdAndStatusIn(resourceId, ACTIVE_STATUSES).stream()
					.anyMatch(other -> !other.getId().equals(excludedReservationId) && other.isActive());
			if (!stillReserved) {
				resource.changeStatus(ResourceStatus.AVAILABLE);
			}
			resourceRepository.save(resource);
		});
	}

	private void checkTeamAvailability(Long teamId, LocalDateTime start, LocalDateTime end,
			Long excludedReservationId) {
		Team team = teamRepository.findById(teamId)
				.orElseThrow(() -> new ResourceNotFoundException("Team " + teamId + " not found"));
		if (!team.isAvailable()) {
			throw new BusinessRuleException("Team " + teamId + " is not available");
		}
		ResourceReservation probe = ResourceReservation.builder()
				.id(excludedReservationId)
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
