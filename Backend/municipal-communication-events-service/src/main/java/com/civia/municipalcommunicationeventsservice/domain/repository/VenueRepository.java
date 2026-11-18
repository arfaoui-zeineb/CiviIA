package com.civia.municipalcommunicationeventsservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.municipalcommunicationeventsservice.domain.model.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
