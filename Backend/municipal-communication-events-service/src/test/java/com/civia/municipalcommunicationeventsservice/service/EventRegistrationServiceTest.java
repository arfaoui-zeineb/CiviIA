package com.civia.municipalcommunicationeventsservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.civia.municipalcommunicationeventsservice.domain.model.Event;
import com.civia.municipalcommunicationeventsservice.domain.model.EventRegistration;
import com.civia.municipalcommunicationeventsservice.domain.model.EventStatus;
import com.civia.municipalcommunicationeventsservice.domain.model.RegistrationStatus;
import com.civia.municipalcommunicationeventsservice.domain.repository.EventRegistrationRepository;
import com.civia.municipalcommunicationeventsservice.domain.repository.EventRepository;
import com.civia.municipalcommunicationeventsservice.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventRegistrationServiceTest {

	@Mock
	private EventRegistrationRepository registrationRepository;
	@Mock
	private EventRepository eventRepository;

	private EventRegistrationService service;

	@BeforeEach
	void setUp() {
		service = new EventRegistrationService(registrationRepository, eventRepository);
	}

	private Event openEvent(int capacity, int registeredCount) {
		return Event.builder()
				.id(1L)
				.title("Forum")
				.startDate(LocalDateTime.of(2026, 12, 1, 10, 0))
				.endDate(LocalDateTime.of(2026, 12, 1, 12, 0))
				.capacity(capacity)
				.registeredCount(registeredCount)
				.status(EventStatus.OPEN)
				.build();
	}

	@Test
	void registerIncrementsCounter() {
		Event event = openEvent(2, 0);
		when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
		when(registrationRepository.existsByEventIdAndUserIdAndStatus(1L, 7L, RegistrationStatus.REGISTERED))
				.thenReturn(false);
		when(registrationRepository.save(any(EventRegistration.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		EventRegistration registration = service.register(1L, 7L);

		assertThat(registration.getStatus()).isEqualTo(RegistrationStatus.REGISTERED);
		assertThat(event.getRegisteredCount()).isEqualTo(1);
	}

	@Test
	void registerIsRefusedWhenEventIsFull() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(openEvent(1, 1)));

		assertThatThrownBy(() -> service.register(1L, 7L)).isInstanceOf(ConflictException.class);
	}

	@Test
	void registerIsRefusedForDuplicateActiveRegistration() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(openEvent(5, 1)));
		when(registrationRepository.existsByEventIdAndUserIdAndStatus(1L, 7L, RegistrationStatus.REGISTERED))
				.thenReturn(true);

		assertThatThrownBy(() -> service.register(1L, 7L)).isInstanceOf(ConflictException.class);
	}

	@Test
	void cancelDecrementsCounterAndReopensEvent() {
		Event event = openEvent(1, 1);
		event.setStatus(EventStatus.FULL);
		EventRegistration registration = EventRegistration.builder()
				.id(3L).eventId(1L).userId(7L).status(RegistrationStatus.REGISTERED).build();
		when(registrationRepository.findById(3L)).thenReturn(Optional.of(registration));
		when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
		when(registrationRepository.save(any(EventRegistration.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		EventRegistration cancelled = service.cancel(3L);

		assertThat(cancelled.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
		assertThat(event.getRegisteredCount()).isZero();
		assertThat(event.getStatus()).isEqualTo(EventStatus.OPEN);
	}
}
