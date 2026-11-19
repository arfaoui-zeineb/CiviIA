package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.ResourceType;
import com.civia.resourcelogisticsservice.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceReservationServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 20, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 11, 20, 12, 0);

    @Mock
    ResourceReservationRepository reservationRepo;

    @Mock
    ResourceRepository resourceRepo;

    @InjectMocks
    ResourceReservationService reservationService;

    @Test
    void reservationRequiresResourceId() {
        ResourceReservation reservation = reservation(null, START, END, 7L);

        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservation));
    }

    @Test
    void reservationRequiresInterventionId() {
        ResourceReservation reservation = reservation(10L, START, END, null);

        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservation));
    }

    @Test
    void reservationRequiresAValidTimeSlot() {
        ResourceReservation reservation = reservation(10L, END, START, 7L);

        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservation));
    }

    @Test
    void overlappingReservationIsRejected() {
        ResourceReservation existing = reservation(10L, START.plusHours(1), END.plusHours(1), 7L);
        ResourceReservation incoming = reservation(10L, START, END, 8L);
        when(resourceRepo.findById(10L)).thenReturn(Optional.of(resource(ResourceStatus.AVAILABLE)));
        when(reservationRepo.findByResourceIdAndStatusIn(10L, ReservationStatus.ACTIVE))
                .thenReturn(List.of(existing));

        assertThrows(IllegalStateException.class, () -> reservationService.addReservation(incoming));
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void reserveConfirmsReservationAndMarksResourceReserved() {
        ResourceReservation reservation = reservation(10L, START, END, 7L);
        Resource resource = resource(ResourceStatus.AVAILABLE);
        when(resourceRepo.findById(10L)).thenReturn(Optional.of(resource));
        when(reservationRepo.findByResourceIdAndStatusIn(10L, ReservationStatus.ACTIVE))
                .thenReturn(List.of());
        when(resourceRepo.save(any())).thenReturn(resource);
        when(reservationRepo.save(any())).thenReturn(reservation);

        ResourceReservation confirmed = reservationService.reserve(reservation);

        assertEquals(ReservationStatus.CONFIRMED, confirmed.getStatus());
        assertEquals(ResourceStatus.RESERVED, resource.getStatus());
    }

    @Test
    void cancelReleasesTheResource() {
        ResourceReservation reservation = reservation(10L, START, END, 7L);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        Resource resource = resource(ResourceStatus.RESERVED);
        when(reservationRepo.findById(5L)).thenReturn(Optional.of(reservation));
        when(resourceRepo.findById(10L)).thenReturn(Optional.of(resource));
        when(reservationRepo.save(any())).thenReturn(reservation);

        ResourceReservation cancelled = reservationService.cancelReservation(5L);

        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
        assertEquals(ResourceStatus.AVAILABLE, resource.getStatus());
    }

    @Test
    void unknownReservationIsReported() {
        when(reservationRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> reservationService.retrieveReservation(99L));
    }

    private ResourceReservation reservation(Long resourceId, LocalDateTime start,
                                            LocalDateTime end, Long interventionId) {
        ResourceReservation r = new ResourceReservation();
        r.setResourceId(resourceId);
        r.setInterventionId(interventionId);
        r.setStartDateTime(start);
        r.setEndDateTime(end);
        return r;
    }

    private Resource resource(ResourceStatus status) {
        return new Resource(10L, "Truck 12", ResourceType.VEHICLE, status, "central depot");
    }
}
