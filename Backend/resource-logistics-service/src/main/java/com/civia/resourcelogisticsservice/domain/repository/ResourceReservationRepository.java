package com.civia.resourcelogisticsservice.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.resourcelogisticsservice.domain.model.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.model.ResourceReservation;

public interface ResourceReservationRepository extends JpaRepository<ResourceReservation, Long> {

	List<ResourceReservation> findByResourceIdAndStatusIn(Long resourceId, Collection<ReservationStatus> statuses);

	List<ResourceReservation> findByTeamIdAndStatusIn(Long teamId, Collection<ReservationStatus> statuses);

	List<ResourceReservation> findByInterventionId(Long interventionId);
}
