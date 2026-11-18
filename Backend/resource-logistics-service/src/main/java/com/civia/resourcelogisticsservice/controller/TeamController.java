package com.civia.resourcelogisticsservice.controller;

import java.time.LocalDateTime;
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

import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.service.ITeamService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

	private final ITeamService teamService;

	@PostMapping
	public Team addTeam(@RequestBody Team team) {
		return teamService.addTeam(team);
	}

	@GetMapping
	public List<Team> retrieveAllTeams() {
		return teamService.retrieveAllTeams();
	}

	@GetMapping("/{id}")
	public Team retrieveTeam(@PathVariable Long id) {
		return teamService.retrieveTeam(id);
	}

	@PutMapping("/{id}")
	public Team updateTeam(@PathVariable Long id, @RequestBody Team team) {
		team.setId(id);
		return teamService.updateTeam(team);
	}

	@DeleteMapping("/{id}")
	public void removeTeam(@PathVariable Long id) {
		teamService.removeTeam(id);
	}

	@GetMapping("/{id}/availability")
	public boolean isTeamAvailable(@PathVariable Long id,
			@RequestParam LocalDateTime startDateTime, @RequestParam LocalDateTime endDateTime) {
		return teamService.isTeamAvailable(id, startDateTime, endDateTime);
	}
}
