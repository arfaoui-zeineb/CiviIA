package com.civicpulse.communication.repository;

import com.civicpulse.communication.domain.entity.EventRegistration;
import com.civicpulse.communication.domain.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    boolean existsByEventIdAndUserIdAndStatus(Long eventId, Long userId, RegistrationStatus status);

    Optional<EventRegistration> findByEventIdAndUserIdAndStatus(Long eventId, Long userId, RegistrationStatus status);

    Page<EventRegistration> findByEventId(Long eventId, Pageable pageable);

    Page<EventRegistration> findByUserId(Long userId, Pageable pageable);
}

