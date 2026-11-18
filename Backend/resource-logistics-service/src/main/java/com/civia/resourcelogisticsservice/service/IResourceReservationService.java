package com.civia.resourcelogisticsservice.service;

import java.util.List;

import com.civia.resourcelogisticsservice.domain.ResourceReservation;

public interface IResourceReservationService {

	List<ResourceReservation> retrieveAllReservations();

	ResourceReservation addReservation(ResourceReservation reservation);

	ResourceReservation updateReservation(ResourceReservation reservation);

	ResourceReservation retrieveReservation(Long idReservation);

	void removeReservation(Long idReservation);

	List<ResourceReservation> retrieveReservationsByIntervention(Long interventionId);

	ResourceReservation cancelReservation(Long idReservation);
}
