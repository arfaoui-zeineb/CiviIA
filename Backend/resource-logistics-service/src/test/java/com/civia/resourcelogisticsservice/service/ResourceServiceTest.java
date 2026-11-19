package com.civia.resourcelogisticsservice.service;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 11, 20, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 11, 20, 12, 0);

    @Mock
    ResourceRepository resourceRepo;

    @Mock
    ResourceReservationRepository reservationRepo;

    @InjectMocks
    ResourceService resourceService;

    @Test
    void resourceInMaintenanceIsNotAvailable() {
        when(resourceRepo.findById(1L)).thenReturn(Optional.of(resource(ResourceStatus.MAINTENANCE)));

        assertFalse(resourceService.isResourceAvailable(1L));
    }

    @Test
    void resourceWithOverlappingReservationIsNotAvailableOnTheSlot() {
        ResourceReservation reservation = new ResourceReservation();
        reservation.setStartDateTime(START.minusHours(1));
        reservation.setEndDateTime(START.plusHours(1));
        when(resourceRepo.findById(1L)).thenReturn(Optional.of(resource(ResourceStatus.AVAILABLE)));
        when(reservationRepo.findByResourceIdAndStatusIn(eq(1L), any())).thenReturn(List.of(reservation));

        assertFalse(resourceService.isResourceAvailable(1L, START, END));
    }

    @Test
    void resourceWithAdjacentReservationIsAvailableOnTheSlot() {
        ResourceReservation reservation = new ResourceReservation();
        reservation.setStartDateTime(END);
        reservation.setEndDateTime(END.plusHours(2));
        when(resourceRepo.findById(1L)).thenReturn(Optional.of(resource(ResourceStatus.AVAILABLE)));
        when(reservationRepo.findByResourceIdAndStatusIn(eq(1L), any())).thenReturn(List.of(reservation));

        assertTrue(resourceService.isResourceAvailable(1L, START, END));
    }

    @Test
    void changeStatusUpdatesTheResource() {
        Resource resource = resource(ResourceStatus.AVAILABLE);
        when(resourceRepo.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceRepo.save(any())).thenReturn(resource);

        Resource updated = resourceService.changeResourceStatus(1L, ResourceStatus.MAINTENANCE);

        assertEquals(ResourceStatus.MAINTENANCE, updated.getStatus());
    }

    private Resource resource(ResourceStatus status) {
        return new Resource(1L, "Truck 12", ResourceType.VEHICLE, status, "central depot");
    }
}
