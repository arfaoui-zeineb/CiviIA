package com.civia.resourcelogisticsservice.service;

import java.time.LocalDateTime;
import java.util.List;

import com.civia.resourcelogisticsservice.domain.Team;

public interface ITeamService {

	List<Team> retrieveAllTeams();

	Team addTeam(Team team);

	Team updateTeam(Team team);

	Team retrieveTeam(Long idTeam);

	void removeTeam(Long idTeam);

	boolean isTeamAvailable(Long idTeam, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
