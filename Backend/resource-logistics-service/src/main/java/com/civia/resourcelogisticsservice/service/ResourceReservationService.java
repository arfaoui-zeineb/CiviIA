package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ResourceReservationService implements IResourceReservationService {

    final ResourceReservationRepository reservationRepo;
    final ResourceRepository resourceRepo;

    @Override
    public List<ResourceReservation> retrieveAllReservations() {
        return (List<ResourceReservation>) reservationRepo.findAll();
    }

    @Override
    public ResourceReservation addReservation(ResourceReservation reservation) {
        validate(reservation);
        if (!checkAvailability(reservation.getResourceId(),
                reservation.getStartDateTime(), reservation.getEndDateTime())) {
            throw new IllegalStateException("Resource is not available on the requested time slot");
        }
        reservation.setStatus(ReservationStatus.PENDING);
        return reservationRepo.save(reservation);
    }

    @Override
    public ResourceReservation updateReservation(ResourceReservation reservation) {
        return reservationRepo.save(reservation);
    }

    @Override
    public ResourceReservation retrieveReservation(Long idReservation) {
        return reservationRepo.findById(idReservation)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found: " + idReservation));
    }

    @Override
    public void removeReservation(Long idReservation) {
        reservationRepo.deleteById(idReservation);
    }

    @Override
    public boolean checkAvailability(Long resourceId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return isSlotFree(resourceId, startDateTime, endDateTime, null);
    }

    @Override
    @Transactional
    public ResourceReservation reserve(ResourceReservation reservation) {
        Resource resource = resourceRepo.findById(reservation.getResourceId())
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + reservation.getResourceId()));
        if (resource.getStatus() != ResourceStatus.AVAILABLE
                && resource.getStatus() != ResourceStatus.RESERVED) {
            throw new IllegalStateException("Resource cannot be reserved while " + resource.getStatus());
        }
        if (!isSlotFree(reservation.getResourceId(), reservation.getStartDateTime(),
                reservation.getEndDateTime(), reservation.getId())) {
            throw new IllegalStateException("Resource is already reserved on this time slot");
        }
        reservation.reserve();
        resource.changeStatus(ResourceStatus.RESERVED);
        resourceRepo.save(resource);
        return reservationRepo.save(reservation);
    }

    @Override
    @Transactional
    public ResourceReservation cancelReservation(Long idReservation) {
        ResourceReservation reservation = retrieveReservation(idReservation);
        reservation.cancel();
        resourceRepo.findById(reservation.getResourceId()).ifPresent(resource -> {
            resource.changeStatus(ResourceStatus.AVAILABLE);
            resourceRepo.save(resource);
        });
        return reservationRepo.save(reservation);
    }

    private void validate(ResourceReservation reservation) {
        if (reservation.getResourceId() == null) {
            throw new IllegalArgumentException("resourceId is required");
        }
        if (reservation.getInterventionId() == null) {
            throw new IllegalArgumentException("interventionId is required");
        }
        LocalDateTime start = reservation.getStartDateTime();
        LocalDateTime end = reservation.getEndDateTime();
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("A reservation needs a valid startDateTime before endDateTime");
        }
        resourceRepo.findById(reservation.getResourceId())
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + reservation.getResourceId()));
    }

    private boolean isSlotFree(Long resourceId, LocalDateTime start, LocalDateTime end, Long excludedId) {
        return reservationRepo.findByResourceIdAndStatusIn(resourceId, ReservationStatus.ACTIVE)
                .stream()
                .filter(r -> excludedId == null || !Objects.equals(r.getId(), excludedId))
                .noneMatch(r -> r.overlaps(start, end));
    }
}
