package com.civicpulse.communication.service.impl;

import com.civicpulse.communication.domain.entity.Category;
import com.civicpulse.communication.domain.entity.Event;
import com.civicpulse.communication.domain.entity.EventRegistration;
import com.civicpulse.communication.domain.entity.Venue;
import com.civicpulse.communication.domain.enums.EventStatus;
import com.civicpulse.communication.domain.enums.RegistrationStatus;
import com.civicpulse.communication.dto.request.EventRequest;
import com.civicpulse.communication.dto.response.EventRegistrationResponse;
import com.civicpulse.communication.dto.response.EventResponse;
import com.civicpulse.communication.exception.BusinessRuleException;
import com.civicpulse.communication.exception.ConflictException;
import com.civicpulse.communication.exception.ResourceNotFoundException;
import com.civicpulse.communication.integration.DepartmentClient;
import com.civicpulse.communication.mapper.EventMapper;
import com.civicpulse.communication.mapper.EventRegistrationMapper;
import com.civicpulse.communication.repository.CategoryRepository;
import com.civicpulse.communication.repository.EventRegistrationRepository;
import com.civicpulse.communication.repository.EventRepository;
import com.civicpulse.communication.repository.VenueRepository;
import com.civicpulse.communication.service.EventService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRegistrationRepository registrationRepository;
    private final DepartmentClient departmentClient;
    private final EventMapper eventMapper;
    private final EventRegistrationMapper registrationMapper;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request) {
        // Rule 4: endDate > startDate
        if (request.getEndDate() == null || !request.getEndDate().isAfter(request.getStartDate())) {
            throw new BusinessRuleException("Event end date must be strictly after start date", "INVALID_DATE_RANGE");
        }

        // Rule 7: Category must be active
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        if (!category.isActive()) {
            throw new BusinessRuleException("Cannot create event with an inactive category", "INACTIVE_CATEGORY");
        }

        // Rule 4: If venue defined, capacity <= venue capacity
        Venue venue = null;
        if (request.getVenueId() != null) {
            venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue", request.getVenueId()));
            if (request.getCapacity() > venue.getCapacity()) {
                throw new BusinessRuleException(
                        String.format("Event capacity (%d) cannot exceed venue capacity (%d)",
                                request.getCapacity(), venue.getCapacity()),
                        "CAPACITY_EXCEEDS_VENUE");
            }
        }

        // Department validation
        if (!departmentClient.validateDepartmentExists(request.getDepartmentId())) {
            throw new BusinessRuleException("Department with id " + request.getDepartmentId() + " does not exist", "INVALID_DEPARTMENT");
        }

        Event event = eventMapper.toEntity(request);
        event.setCategory(category);
        event.setVenue(venue);
        event.setStatus(EventStatus.DRAFT);
        event.setRegisteredCount(0);

        Event saved = eventRepository.save(event);
        return eventMapper.toResponse(saved);
    }

    @Override
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        return eventMapper.toResponse(event);
    }

    @Override
    public Page<EventResponse> getAllEvents(Long categoryId, Long departmentId, EventStatus status,
                                           OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("departmentId"), departmentId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return eventRepository.findAll(spec, pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        // Rule 4: Dates check
        if (request.getEndDate() == null || !request.getEndDate().isAfter(request.getStartDate())) {
            throw new BusinessRuleException("Event end date must be strictly after start date", "INVALID_DATE_RANGE");
        }

        // Capacity cannot be reduced below current registrations
        if (request.getCapacity() < event.getRegisteredCount()) {
            throw new BusinessRuleException(
                    String.format("Cannot set capacity (%d) lower than currently registered participants (%d)",
                            request.getCapacity(), event.getRegisteredCount()),
                    "CAPACITY_LESS_THAN_REGISTRATIONS");
        }

        // Category check
        if (!event.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            if (!category.isActive()) {
                throw new BusinessRuleException("Cannot associate an inactive category", "INACTIVE_CATEGORY");
            }
            event.setCategory(category);
        }

        // Venue check
        if (request.getVenueId() != null) {
            Venue venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue", request.getVenueId()));
            if (request.getCapacity() > venue.getCapacity()) {
                throw new BusinessRuleException(
                        String.format("Event capacity (%d) cannot exceed venue capacity (%d)",
                                request.getCapacity(), venue.getCapacity()),
                        "CAPACITY_EXCEEDS_VENUE");
            }
            event.setVenue(venue);
        } else {
            event.setVenue(null);
        }

        // Department check
        if (!event.getDepartmentId().equals(request.getDepartmentId())) {
            if (!departmentClient.validateDepartmentExists(request.getDepartmentId())) {
                throw new BusinessRuleException("Department does not exist", "INVALID_DEPARTMENT");
            }
            event.setDepartmentId(request.getDepartmentId());
        }

        eventMapper.updateEntityFromRequest(request, event);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }
        eventRepository.deleteById(id);
    }

    // --- State Transitions ---

    @Override
    @Transactional
    public EventResponse openEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        // Allowed transitions to OPEN: DRAFT -> OPEN, CLOSED -> OPEN
        if (event.getStatus() != EventStatus.DRAFT && event.getStatus() != EventStatus.CLOSED) {
            throw new BusinessRuleException(
                    "Invalid transition: Cannot open an event with status " + event.getStatus(),
                    "INVALID_STATUS_TRANSITION");
        }

        if (event.getRegisteredCount() >= event.getCapacity()) {
            event.setStatus(EventStatus.FULL);
        } else {
            event.setStatus(EventStatus.OPEN);
        }

        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse closeEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        if (event.getStatus() != EventStatus.OPEN && event.getStatus() != EventStatus.FULL) {
            throw new BusinessRuleException(
                    "Invalid transition: Cannot close an event with status " + event.getStatus(),
                    "INVALID_STATUS_TRANSITION");
        }

        event.setStatus(EventStatus.CLOSED);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse cancelEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new BusinessRuleException("Event is already cancelled", "ALREADY_CANCELLED");
        }

        event.setStatus(EventStatus.CANCELLED);
        return eventMapper.toResponse(event);
    }

    // --- Registrations ---

    @Override
    @Transactional
    public EventRegistrationResponse registerUser(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        // Rule 8: Un événement CANCELLED ou CLOSED n'accepte plus d'inscription
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.CLOSED) {
            throw new BusinessRuleException(
                    "Cannot register to an event with status " + event.getStatus(),
                    "EVENT_NOT_ACCEPTING_REGISTRATIONS");
        }

        if (event.getStatus() == EventStatus.DRAFT) {
            throw new BusinessRuleException("Cannot register to a draft event", "EVENT_NOT_OPEN");
        }

        // Rule 3: Un utilisateur n'a qu'UNE inscription active par événement
        if (registrationRepository.existsByEventIdAndUserIdAndStatus(eventId, userId, RegistrationStatus.REGISTERED)) {
            throw new ConflictException(
                    "User already has an active registration for this event",
                    "DUPLICATE_REGISTRATION");
        }

        // Rule 1: Inscription refusée si registeredCount >= capacity. Incrément ATOMIQUE conditionnel SQL
        int updatedRows = eventRepository.incrementRegisteredCountIfAvailable(eventId);
        if (updatedRows == 0) {
            // Check why it failed: capacity reached or invalid status
            Event currentEvent = eventRepository.findById(eventId).orElseThrow();
            if (currentEvent.getRegisteredCount() >= currentEvent.getCapacity()) {
                eventRepository.updateStatusIfFull(eventId, EventStatus.FULL);
                throw new ConflictException("Event is fully booked", "EVENT_FULL");
            } else {
                throw new BusinessRuleException("Cannot register to this event", "REGISTRATION_FAILED");
            }
        }

        // If after increment capacity is now reached, set status -> FULL
        eventRepository.updateStatusIfFull(eventId, EventStatus.FULL);

        // Create registration
        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .userId(userId)
                .status(RegistrationStatus.REGISTERED)
                .registeredAt(OffsetDateTime.now())
                .build();

        EventRegistration saved = registrationRepository.save(registration);
        return registrationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EventRegistrationResponse cancelRegistration(Long eventId, Long userId) {
        EventRegistration registration = registrationRepository.findByEventIdAndUserIdAndStatus(
                eventId, userId, RegistrationStatus.REGISTERED)
                .orElseThrow(() -> new ResourceNotFoundException("Active Registration for user " + userId + " on event", eventId));

        // Rule 2: Annulation : renseigne cancelledAt, passe le statut à CANCELLED, décrémente registeredCount
        registration.setStatus(RegistrationStatus.CANCELLED);
        registration.setCancelledAt(OffsetDateTime.now());

        // Atomic decrement
        eventRepository.decrementRegisteredCount(eventId);

        // Repasse FULL -> OPEN si une place se libère
        eventRepository.updateStatusIfReopened(eventId, EventStatus.OPEN);

        return registrationMapper.toResponse(registration);
    }

    @Override
    public Page<EventRegistrationResponse> getEventRegistrations(Long eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", eventId);
        }
        return registrationRepository.findByEventId(eventId, pageable)
                .map(registrationMapper::toResponse);
    }
}

