package com.civia.resourcelogisticsservice.controller;

import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.service.IResourceReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/reservations", "/ResourceReservation"})
public class ResourceReservationController {

    private final IResourceReservationService reservationService;

    @PostMapping({"", "/addReservation"})
    public ResourceReservation addReservation(@RequestBody ResourceReservation reservation) {
        return reservationService.addReservation(reservation);
    }

    @GetMapping({"", "/getReservations"})
    public List<ResourceReservation> getAllReservations() {
        return reservationService.retrieveAllReservations();
    }

    @GetMapping({"/{id}", "/getReservationId/{id}"})
    public ResourceReservation getReservationById(@PathVariable Long id) {
        return reservationService.retrieveReservation(id);
    }

    @PutMapping("/{id}")
    public ResourceReservation updateReservation(@PathVariable Long id,
                                                 @RequestBody ResourceReservation reservation) {
        reservation.setId(id);
        return reservationService.updateReservation(reservation);
    }

    @PutMapping("/updateReservation")
    public ResourceReservation updateReservation(@RequestBody ResourceReservation reservation) {
        return reservationService.updateReservation(reservation);
    }

    @DeleteMapping({"/{id}", "/deleteReservation/{id}"})
    public ResourceReservation cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }

    @PostMapping({"/reserve", "/confirmReservation"})
    public ResourceReservation reserve(@RequestBody ResourceReservation reservation) {
        return reservationService.reserve(reservation);
    }

    @GetMapping({"/checkAvailability", "/isResourceAvailable"})
    public boolean checkAvailability(@RequestParam Long resourceId,
                                     @RequestParam LocalDateTime startDateTime,
                                     @RequestParam LocalDateTime endDateTime) {
        return reservationService.checkAvailability(resourceId, startDateTime, endDateTime);
    }
}
