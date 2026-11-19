package com.civia.resourcelogisticsservice.repository;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ResourceReservationRepository extends CrudRepository<ResourceReservation, Long> {

    List<ResourceReservation> findByResourceIdAndStatusIn(Long resourceId, Collection<ReservationStatus> statuses);

    List<ResourceReservation> findByTeamIdAndStatusIn(Long teamId, Collection<ReservationStatus> statuses);
}
