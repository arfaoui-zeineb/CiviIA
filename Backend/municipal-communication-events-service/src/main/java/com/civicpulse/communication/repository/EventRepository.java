package com.civicpulse.communication.repository;

import com.civicpulse.communication.domain.entity.Event;
import com.civicpulse.communication.domain.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /**
     * Atomic increment of registered_count if space is available (registered_count < capacity).
     * Returns 1 if successful, 0 if capacity reached or event not found.
     */
    @Modifying
    @Query("UPDATE Event e SET e.registeredCount = e.registeredCount + 1 " +
           "WHERE e.id = :eventId AND e.registeredCount < e.capacity AND e.status IN ('OPEN', 'FULL')")
    int incrementRegisteredCountIfAvailable(@Param("eventId") Long eventId);

    /**
     * Atomic decrement of registered_count upon cancellation.
     */
    @Modifying
    @Query("UPDATE Event e SET e.registeredCount = e.registeredCount - 1 " +
           "WHERE e.id = :eventId AND e.registeredCount > 0")
    int decrementRegisteredCount(@Param("eventId") Long eventId);

    /**
     * Update status if capacity is reached.
     */
    @Modifying
    @Query("UPDATE Event e SET e.status = :status WHERE e.id = :eventId AND e.registeredCount >= e.capacity")
    int updateStatusIfFull(@Param("eventId") Long eventId, @Param("status") EventStatus status);

    /**
     * Update status to OPEN if previously FULL and a spot freed up.
     */
    @Modifying
    @Query("UPDATE Event e SET e.status = :status WHERE e.id = :eventId AND e.registeredCount < e.capacity AND e.status = 'FULL'")
    int updateStatusIfReopened(@Param("eventId") Long eventId, @Param("status") EventStatus status);
}

