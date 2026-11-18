package com.civia.municipalcommunicationeventsservice.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.municipalcommunicationeventsservice.domain.model.EventRegistration;
import com.civia.municipalcommunicationeventsservice.domain.model.RegistrationStatus;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

	List<EventRegistration> findByEventId(Long eventId);

	boolean existsByEventIdAndUserIdAndStatus(Long eventId, Long userId, RegistrationStatus status);
}
