package com.civicpulse.communication.service;

import com.civicpulse.communication.domain.enums.EventStatus;
import com.civicpulse.communication.dto.request.EventRequest;
import com.civicpulse.communication.dto.response.EventRegistrationResponse;
import com.civicpulse.communication.dto.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventResponse getEventById(Long id);
    Page<EventResponse> getAllEvents(Long categoryId, Long departmentId, EventStatus status,
                                     OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable);
    EventResponse updateEvent(Long id, EventRequest request);
    void deleteEvent(Long id);

    // Dedicated state transitions
    EventResponse openEvent(Long id);
    EventResponse closeEvent(Long id);
    EventResponse cancelEvent(Long id);

    // Registrations
    EventRegistrationResponse registerUser(Long eventId, Long userId);
    EventRegistrationResponse cancelRegistration(Long eventId, Long userId);
    Page<EventRegistrationResponse> getEventRegistrations(Long eventId, Pageable pageable);
}

