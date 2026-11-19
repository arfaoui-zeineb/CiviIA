package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.ResourceReservation;

import java.time.LocalDateTime;
import java.util.List;

public interface IResourceReservationService {

    List<ResourceReservation> retrieveAllReservations();

    ResourceReservation addReservation(ResourceReservation reservation);

    ResourceReservation updateReservation(ResourceReservation reservation);

    ResourceReservation retrieveReservation(Long idReservation);

    void removeReservation(Long idReservation);

    boolean checkAvailability(Long resourceId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    ResourceReservation reserve(ResourceReservation reservation);

    ResourceReservation cancelReservation(Long idReservation);
}
