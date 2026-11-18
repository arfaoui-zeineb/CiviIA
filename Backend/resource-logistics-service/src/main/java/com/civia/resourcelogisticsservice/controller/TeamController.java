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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Team")
@Tag(name = "Teams", description = "Gestion des equipes")
public class TeamController {

	private final ITeamService teamService;

	@PostMapping("/addTeam")
	@Operation(summary = "addTeam", description = "Creer une equipe")
	public Team addTeam(@RequestBody Team team) {
		return teamService.addTeam(team);
	}

	@GetMapping("/getAllTeams")
	@Operation(summary = "getAllTeams", description = "Recuperer toutes les equipes")
	public List<Team> getAllTeams() {
		return teamService.getAllTeams();
	}

	@GetMapping("/getTeam/{idTeam}")
	@Operation(summary = "getTeam", description = "Recuperer une equipe par identifiant")
	public Team getTeam(@Parameter(name = "idTeam", description = "idTeam", required = true)
			@PathVariable("idTeam") Long idTeam) {
		return teamService.getTeam(idTeam);
	}

	@PutMapping("/updateTeam")
	@Operation(summary = "updateTeam", description = "Remplacer une equipe")
	public Team updateTeam(@RequestBody Team team) {
		return teamService.updateTeam(team);
	}

	@DeleteMapping("/deleteTeam/{idTeam}")
	@Operation(summary = "deleteTeam", description = "Supprimer une equipe")
	public void deleteTeam(@Parameter(name = "idTeam", description = "idTeam", required = true)
			@PathVariable("idTeam") Long idTeam) {
		teamService.deleteTeam(idTeam);
	}

	@GetMapping("/isTeamAvailable/{idTeam}")
	@Operation(summary = "isTeamAvailable", description = "Verifier la disponibilite d'une equipe sur un creneau")
	public boolean isTeamAvailable(
			@Parameter(name = "idTeam", description = "idTeam", required = true)
			@PathVariable("idTeam") Long idTeam,
			@RequestParam LocalDateTime startDateTime, @RequestParam LocalDateTime endDateTime) {
		return teamService.isTeamAvailable(idTeam, startDateTime, endDateTime);
	}
}
