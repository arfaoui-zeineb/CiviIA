package com.civia.resourcelogisticsservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.domain.TeamStatus;
import com.civia.resourcelogisticsservice.exception.ResourceNotFoundException;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService implements ITeamService {

	private static final List<ReservationStatus> ACTIVE_STATUSES =
			List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

	private final TeamRepository teamRepository;
	private final ResourceReservationRepository reservationRepository;

	@Override
	public List<Team> retrieveAllTeams() {
		return teamRepository.findAll();
	}

	@Override
	@Transactional
	public Team addTeam(Team team) {
		if (team.getStatus() == null) {
			team.setStatus(TeamStatus.AVAILABLE);
		}
		return teamRepository.save(team);
	}

	@Override
	@Transactional
	public Team updateTeam(Team team) {
		retrieveTeam(team.getId());
		return teamRepository.save(team);
	}

	@Override
	public Team retrieveTeam(Long idTeam) {
		return teamRepository.findById(idTeam)
				.orElseThrow(() -> new ResourceNotFoundException("Team " + idTeam + " not found"));
	}

	@Override
	@Transactional
	public void removeTeam(Long idTeam) {
		teamRepository.deleteById(idTeam);
	}

	@Override
	public boolean isTeamAvailable(Long idTeam, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		validatePeriod(startDateTime, endDateTime);
		Team team = retrieveTeam(idTeam);
		if (!team.isAvailable()) {
			return false;
		}
		ResourceReservation probe = ResourceReservation.builder()
				.teamId(idTeam)
				.startDateTime(startDateTime)
				.endDateTime(endDateTime)
				.build();
		return reservationRepository.findByTeamIdAndStatusIn(idTeam, ACTIVE_STATUSES).stream()
				.noneMatch(probe::overlaps);
	}

	private void validatePeriod(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null || !start.isBefore(end)) {
			throw new IllegalArgumentException("startDateTime must be before endDateTime");
		}
	}
}
