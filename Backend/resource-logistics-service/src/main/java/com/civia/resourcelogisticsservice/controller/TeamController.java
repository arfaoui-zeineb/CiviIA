package com.civia.resourcelogisticsservice.controller;

import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.service.ITeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/teams", "/Team"})
public class TeamController {

    private final ITeamService teamService;

    @PostMapping({"", "/addTeam"})
    public Team addTeam(@RequestBody Team team) {
        return teamService.addTeam(team);
    }

    @GetMapping({"", "/getTeams"})
    public List<Team> getAllTeams() {
        return teamService.retrieveAllTeams();
    }

    @GetMapping({"/{id}", "/getTeamId/{id}"})
    public Team getTeamById(@PathVariable Long id) {
        return teamService.retrieveTeam(id);
    }

    @PutMapping("/{id}")
    public Team updateTeam(@PathVariable Long id, @RequestBody Team team) {
        team.setId(id);
        return teamService.updateTeam(team);
    }

    @PutMapping("/updateTeam")
    public Team updateTeam(@RequestBody Team team) {
        return teamService.updateTeam(team);
    }

    @DeleteMapping({"/{id}", "/deleteTeam/{id}"})
    public void deleteTeam(@PathVariable Long id) {
        teamService.removeTeam(id);
    }

    @GetMapping({"/{id}/availability", "/isTeamAvailable/{id}"})
    public boolean isTeamAvailable(@PathVariable Long id,
                                   @RequestParam LocalDateTime startDateTime,
                                   @RequestParam LocalDateTime endDateTime) {
        return teamService.isTeamAvailable(id, startDateTime, endDateTime);
    }
}
