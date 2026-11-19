package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TeamService implements ITeamService {

    final TeamRepository teamRepo;
    final ResourceReservationRepository reservationRepo;

    @Override
    public List<Team> retrieveAllTeams() {
        return (List<Team>) teamRepo.findAll();
    }

    @Override
    public Team addTeam(Team team) {
        return teamRepo.save(team);
    }

    @Override
    public Team updateTeam(Team team) {
        return teamRepo.save(team);
    }

    @Override
    public Team retrieveTeam(Long idTeam) {
        return teamRepo.findById(idTeam)
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + idTeam));
    }

    @Override
    public void removeTeam(Long idTeam) {
        teamRepo.deleteById(idTeam);
    }

    @Override
    public boolean isTeamAvailable(Long idTeam, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Team team = retrieveTeam(idTeam);
        if (!team.isAvailable()) {
            return false;
        }
        List<ResourceReservation> reservations =
                reservationRepo.findByTeamIdAndStatusIn(idTeam, ReservationStatus.ACTIVE);
        return reservations.stream().noneMatch(r -> r.overlaps(startDateTime, endDateTime));
    }
}
