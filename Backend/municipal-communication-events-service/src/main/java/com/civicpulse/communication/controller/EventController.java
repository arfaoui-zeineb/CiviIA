package com.civicpulse.communication.controller;

import com.civicpulse.communication.domain.enums.EventStatus;
import com.civicpulse.communication.dto.request.EventRequest;
import com.civicpulse.communication.dto.response.EventRegistrationResponse;
import com.civicpulse.communication.dto.response.EventResponse;
import com.civicpulse.communication.security.SecurityUtils;
import com.civicpulse.communication.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Endpoints for managing municipal events and citizen registrations")
public class EventController {

    private final EventService eventService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Create an event in DRAFT status")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an event by ID")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping
    @Operation(summary = "Get events with filtering (category, department, status, dates) and pagination")
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(categoryId, departmentId, status, startDate, endDate, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // --- State Transitions ---

    @PostMapping("/{id}/open")
    @Operation(summary = "Open an event for citizen registrations")
    public ResponseEntity<EventResponse> openEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.openEvent(id));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close an event")
    public ResponseEntity<EventResponse> closeEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.closeEvent(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an event")
    public ResponseEntity<EventResponse> cancelEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.cancelEvent(id));
    }

    // --- Registrations ---

    @PostMapping("/{id}/registrations")
    @Operation(summary = "Register authenticated citizen to an event (userId extracted from JWT)")
    public ResponseEntity<EventRegistrationResponse> registerUser(@PathVariable Long id) {
        Long currentUserId = securityUtils.getCurrentUserId();
        EventRegistrationResponse response = eventService.registerUser(id, currentUserId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{registrationId}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}/registrations")
    @Operation(summary = "Get list of registrations for an event")
    public ResponseEntity<Page<EventRegistrationResponse>> getEventRegistrations(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventRegistrations(id, pageable));
    }

    @PostMapping("/{id}/registrations/cancel")
    @Operation(summary = "Cancel current citizen registration for an event (via POST)")
    public ResponseEntity<EventRegistrationResponse> cancelRegistrationPost(@PathVariable Long id) {
        Long currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(eventService.cancelRegistration(id, currentUserId));
    }

    @DeleteMapping("/{id}/registrations")
    @Operation(summary = "Cancel current citizen registration for an event (via DELETE)")
    public ResponseEntity<Void> cancelRegistrationDelete(@PathVariable Long id) {
        Long currentUserId = securityUtils.getCurrentUserId();
        eventService.cancelRegistration(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}

