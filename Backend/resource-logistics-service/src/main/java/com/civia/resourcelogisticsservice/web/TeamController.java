package com.civia.resourcelogisticsservice.web;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.civia.resourcelogisticsservice.service.TeamService;
import com.civia.resourcelogisticsservice.web.dto.AvailabilityResponse;
import com.civia.resourcelogisticsservice.web.dto.TeamRequest;
import com.civia.resourcelogisticsservice.web.dto.TeamResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

	private final TeamService teamService;

	public TeamController(TeamService teamService) {
		this.teamService = teamService;
	}

	@GetMapping
	public List<TeamResponse> findAll() {
		return teamService.findAll().stream().map(TeamResponse::from).toList();
	}

	@GetMapping("/{id}")
	public TeamResponse findById(@PathVariable Long id) {
		return TeamResponse.from(teamService.findById(id));
	}

	@PostMapping
	public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
		TeamResponse created = TeamResponse.from(teamService.create(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public TeamResponse update(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
		return TeamResponse.from(teamService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		teamService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/availability")
	public AvailabilityResponse isAvailable(@PathVariable Long id,
			@RequestParam LocalDateTime startDateTime, @RequestParam LocalDateTime endDateTime) {
		return new AvailabilityResponse(teamService.isAvailable(id, startDateTime, endDateTime));
	}
}
