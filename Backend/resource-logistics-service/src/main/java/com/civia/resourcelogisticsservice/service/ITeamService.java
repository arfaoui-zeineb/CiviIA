package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.Team;

import java.time.LocalDateTime;
import java.util.List;

public interface ITeamService {

    List<Team> retrieveAllTeams();

    Team addTeam(Team team);

    Team updateTeam(Team team);

    Team retrieveTeam(Long idTeam);

    void removeTeam(Long idTeam);

    boolean isTeamAvailable(Long idTeam, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
