package com.civia.resourcelogisticsservice.service;

import java.util.List;

import com.civia.resourcelogisticsservice.domain.ResourceReservation;

public interface IResourceReservationService {

	List<ResourceReservation> getAllReservations();

	ResourceReservation addReservation(ResourceReservation reservation);

	ResourceReservation updateReservation(ResourceReservation reservation);

	ResourceReservation getReservation(Long idReservation);

	void deleteReservation(Long idReservation);

	List<ResourceReservation> getReservationsByIntervention(Long interventionId);

	ResourceReservation cancelReservation(Long idReservation);
}
