package com.civicpulse.communication.service;

import com.civicpulse.communication.domain.entity.Category;
import com.civicpulse.communication.domain.entity.Event;
import com.civicpulse.communication.domain.entity.EventRegistration;
import com.civicpulse.communication.domain.entity.Venue;
import com.civicpulse.communication.domain.enums.EventStatus;
import com.civicpulse.communication.domain.enums.RegistrationStatus;
import com.civicpulse.communication.dto.request.EventRequest;
import com.civicpulse.communication.exception.BusinessRuleException;
import com.civicpulse.communication.exception.ConflictException;
import com.civicpulse.communication.integration.DepartmentClient;
import com.civicpulse.communication.mapper.EventMapper;
import com.civicpulse.communication.mapper.EventRegistrationMapper;
import com.civicpulse.communication.repository.CategoryRepository;
import com.civicpulse.communication.repository.EventRegistrationRepository;
import com.civicpulse.communication.repository.EventRepository;
import com.civicpulse.communication.repository.VenueRepository;
import com.civicpulse.communication.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventBusinessRulesTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventRegistrationRepository registrationRepository;

    @Mock
    private DepartmentClient departmentClient;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    private EventRegistrationMapper registrationMapper = Mappers.getMapper(EventRegistrationMapper.class);

    @InjectMocks
    private EventServiceImpl eventService;

    private Category activeCategory;
    private Category inactiveCategory;
    private Venue venue;
    private Event openEvent;

    @BeforeEach
    void setUp() {
        activeCategory = Category.builder().id(1L).name("Culture").active(true).build();
        inactiveCategory = Category.builder().id(2L).name("Sports").active(false).build();
        venue = Venue.builder().id(10L).name("Salle des Fêtes").capacity(100).build();

        openEvent = Event.builder()
                .id(100L)
                .category(activeCategory)
                .venue(venue)
                .departmentId(5L)
                .title("Concert Municipal")
                .description("Musique classique")
                .startDate(OffsetDateTime.now().plusDays(2))
                .endDate(OffsetDateTime.now().plusDays(3))
                .capacity(50)
                .registeredCount(10)
                .status(EventStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("Règle 7: Refus de création si la catégorie est inactive")
    void shouldRejectCreationWhenCategoryIsInactive() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(inactiveCategory));

        EventRequest request = EventRequest.builder()
                .categoryId(2L)
                .departmentId(5L)
                .title("Course")
                .description("Marathon")
                .startDate(OffsetDateTime.now().plusDays(1))
                .endDate(OffsetDateTime.now().plusDays(2))
                .capacity(20)
                .build();

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive category");
    }

    @Test
    @DisplayName("Règle 4: endDate doit être postérieure à startDate")
    void shouldRejectWhenEndDateIsBeforeStartDate() {
        EventRequest request = EventRequest.builder()
                .categoryId(1L)
                .departmentId(5L)
                .title("Séance")
                .description("Desc")
                .startDate(OffsetDateTime.now().plusDays(5))
                .endDate(OffsetDateTime.now().plusDays(4)) // Earlier!
                .capacity(20)
                .build();

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("strictly after start date");
    }

    @Test
    @DisplayName("Règle 4: Si venueId défini, capacity <= capacity du Venue")
    void shouldRejectWhenCapacityExceedsVenueCapacity() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(activeCategory));
        when(venueRepository.findById(10L)).thenReturn(Optional.of(venue)); // Venue capacity = 100

        EventRequest request = EventRequest.builder()
                .categoryId(1L)
                .venueId(10L)
                .departmentId(5L)
                .title("Grand Gala")
                .description("Desc")
                .startDate(OffsetDateTime.now().plusDays(1))
                .endDate(OffsetDateTime.now().plusDays(2))
                .capacity(150) // > 100 !
                .build();

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot exceed venue capacity");
    }

    @Test
    @DisplayName("Règle 3: Un utilisateur n'a qu'UNE inscription active par événement")
    void shouldRejectDuplicateRegistrationForSameUser() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(openEvent));
        when(registrationRepository.existsByEventIdAndUserIdAndStatus(100L, 42L, RegistrationStatus.REGISTERED))
                .thenReturn(true);

        assertThatThrownBy(() -> eventService.registerUser(100L, 42L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already has an active registration");
    }

    @Test
    @DisplayName("Règle 1: Inscription refusée si registeredCount >= capacity")
    void shouldRejectRegistrationWhenEventIsFull() {
        when(eventRepository.findById(100L)).thenReturn(Optional.of(openEvent));
        when(registrationRepository.existsByEventIdAndUserIdAndStatus(100L, 42L, RegistrationStatus.REGISTERED))
                .thenReturn(false);
        // Simulate SQL atomic update failure (capacity reached)
        when(eventRepository.incrementRegisteredCountIfAvailable(100L)).thenReturn(0);

        openEvent.setRegisteredCount(50);
        openEvent.setCapacity(50);

        assertThatThrownBy(() -> eventService.registerUser(100L, 42L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("fully booked");

        verify(eventRepository).updateStatusIfFull(100L, EventStatus.FULL);
    }

    @Test
    @DisplayName("Règle 8: Un événement CANCELLED ou CLOSED n'accepte plus d'inscription")
    void shouldRejectRegistrationWhenEventClosedOrCancelled() {
        openEvent.setStatus(EventStatus.CLOSED);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(openEvent));

        assertThatThrownBy(() -> eventService.registerUser(100L, 42L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot register to an event with status CLOSED");

        openEvent.setStatus(EventStatus.CANCELLED);
        assertThatThrownBy(() -> eventService.registerUser(100L, 42L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot register to an event with status CANCELLED");
    }

    @Test
    @DisplayName("Règle 2: Annulation renseigne cancelledAt, statut CANCELLED, et décrémente registeredCount")
    void shouldProperlyCancelRegistration() {
        EventRegistration activeReg = EventRegistration.builder()
                .id(200L)
                .event(openEvent)
                .userId(42L)
                .status(RegistrationStatus.REGISTERED)
                .registeredAt(OffsetDateTime.now().minusHours(2))
                .build();

        when(registrationRepository.findByEventIdAndUserIdAndStatus(100L, 42L, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(activeReg));

        var response = eventService.cancelRegistration(100L, 42L);

        assertThat(response.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
        assertThat(activeReg.getCancelledAt()).isNotNull();
        verify(eventRepository).decrementRegisteredCount(100L);
        verify(eventRepository).updateStatusIfReopened(100L, EventStatus.OPEN);
    }

    @Test
    @DisplayName("Règle 6: Transitions par actions dédiées - transition invalide -> 422")
    void shouldRejectInvalidStatusTransition() {
        openEvent.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findById(100L)).thenReturn(Optional.of(openEvent));

        // CANCELLED -> OPEN is invalid
        assertThatThrownBy(() -> eventService.openEvent(100L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot open an event with status CANCELLED");
    }
}

