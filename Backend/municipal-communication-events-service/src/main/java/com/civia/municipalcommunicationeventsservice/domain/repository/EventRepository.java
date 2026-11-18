package com.civia.municipalcommunicationeventsservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.municipalcommunicationeventsservice.domain.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
