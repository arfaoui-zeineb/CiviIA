package com.civia.resourcelogisticsservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.resourcelogisticsservice.domain.model.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.model.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.model.Team;
import com.civia.resourcelogisticsservice.domain.model.TeamStatus;
import com.civia.resourcelogisticsservice.domain.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.domain.repository.TeamRepository;
import com.civia.resourcelogisticsservice.exception.ResourceNotFoundException;
import com.civia.resourcelogisticsservice.web.dto.TeamRequest;

@Service
@Transactional(readOnly = true)
public class TeamService {

	private static final List<ReservationStatus> ACTIVE_STATUSES =
			List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

	private final TeamRepository teamRepository;
	private final ResourceReservationRepository reservationRepository;

	public TeamService(TeamRepository teamRepository, ResourceReservationRepository reservationRepository) {
		this.teamRepository = teamRepository;
		this.reservationRepository = reservationRepository;
	}

	public List<Team> findAll() {
		return teamRepository.findAll();
	}

	public Team findById(Long id) {
		return teamRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Team " + id + " not found"));
	}

	@Transactional
	public Team create(TeamRequest request) {
		Team team = Team.builder()
				.name(request.name())
				.specialty(request.specialty())
				.zone(request.zone())
				.status(request.status() != null ? request.status() : TeamStatus.AVAILABLE)
				.build();
		return teamRepository.save(team);
	}

	@Transactional
	public Team update(Long id, TeamRequest request) {
		Team team = findById(id);
		team.setName(request.name());
		team.setSpecialty(request.specialty());
		team.setZone(request.zone());
		team.setStatus(request.status() != null ? request.status() : team.getStatus());
		return teamRepository.save(team);
	}

	@Transactional
	public void delete(Long id) {
		teamRepository.delete(findById(id));
	}

	/**
	 * Une equipe est disponible si son statut l'autorise ET si elle n'a aucune
	 * reservation active sur le creneau demande (dossier technique § 4.5).
	 */
	public boolean isAvailable(Long id, LocalDateTime start, LocalDateTime end) {
		validatePeriod(start, end);
		Team team = findById(id);
		if (!team.isAvailable()) {
			return false;
		}
		ResourceReservation probe = ResourceReservation.builder()
				.teamId(id)
				.startDateTime(start)
				.endDateTime(end)
				.build();
		return reservationRepository.findByTeamIdAndStatusIn(id, ACTIVE_STATUSES).stream()
				.noneMatch(probe::overlaps);
	}

	private void validatePeriod(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null || !start.isBefore(end)) {
			throw new IllegalArgumentException("startDateTime must be before endDateTime");
		}
	}
}
