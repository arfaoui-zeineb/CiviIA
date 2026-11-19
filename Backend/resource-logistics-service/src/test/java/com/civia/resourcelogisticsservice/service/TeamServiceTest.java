package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.domain.TeamStatus;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 20, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 11, 20, 12, 0);

    @Mock
    TeamRepository teamRepo;

    @Mock
    ResourceReservationRepository reservationRepo;

    @InjectMocks
    TeamService teamService;

    @Test
    void busyTeamIsNotAvailable() {
        Team team = new Team(1L, "Team A", "cleaning", "north", TeamStatus.BUSY);
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));

        assertFalse(teamService.isTeamAvailable(1L, START, END));
    }

    @Test
    void teamWithOverlappingReservationIsNotAvailable() {
        Team team = new Team(1L, "Team A", "cleaning", "north", TeamStatus.AVAILABLE);
        ResourceReservation reservation = reservation(START.plusHours(1), END.plusHours(1));
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));
        when(reservationRepo.findByTeamIdAndStatusIn(eq(1L), any())).thenReturn(List.of(reservation));

        assertFalse(teamService.isTeamAvailable(1L, START, END));
    }

    @Test
    void teamWithNonOverlappingReservationIsAvailable() {
        Team team = new Team(1L, "Team A", "cleaning", "north", TeamStatus.AVAILABLE);
        ResourceReservation reservation = reservation(END, END.plusHours(2));
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));
        when(reservationRepo.findByTeamIdAndStatusIn(eq(1L), any())).thenReturn(List.of(reservation));

        assertTrue(teamService.isTeamAvailable(1L, START, END));
    }

    private ResourceReservation reservation(LocalDateTime start, LocalDateTime end) {
        ResourceReservation r = new ResourceReservation();
        r.setTeamId(1L);
        r.setStartDateTime(start);
        r.setEndDateTime(end);
        r.setStatus(ReservationStatus.CONFIRMED);
        return r;
    }
}
