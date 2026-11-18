package com.civia.resourcelogisticsservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.civia.resourcelogisticsservice.domain.model.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.model.Resource;
import com.civia.resourcelogisticsservice.domain.model.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.model.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.model.ResourceType;
import com.civia.resourcelogisticsservice.domain.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.domain.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.domain.repository.TeamRepository;
import com.civia.resourcelogisticsservice.exception.ConflictException;
import com.civia.resourcelogisticsservice.web.dto.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceReservationServiceTest {

	private static final LocalDateTime START = LocalDateTime.of(2026, 11, 20, 8, 0);
	private static final LocalDateTime END = LocalDateTime.of(2026, 11, 20, 12, 0);

	@Mock
	private ResourceReservationRepository reservationRepository;
	@Mock
	private ResourceRepository resourceRepository;
	@Mock
	private TeamRepository teamRepository;

	private ResourceReservationService service;

	@BeforeEach
	void setUp() {
		service = new ResourceReservationService(reservationRepository, resourceRepository, teamRepository);
	}

	private ReservationRequest request() {
		return new ReservationRequest(10L, 20L, null, START, END);
	}

	private Resource availableResource() {
		return Resource.builder()
				.id(20L).name("Truck").type(ResourceType.VEHICLE)
				.status(ResourceStatus.AVAILABLE).build();
	}

	@Test
	void createConfirmsReservationAndReservesResource() {
		Resource resource = availableResource();
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(resource));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of());
		when(reservationRepository.save(any(ResourceReservation.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ResourceReservation created = service.create(request());

		assertThat(created.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(resource.getStatus()).isEqualTo(ResourceStatus.RESERVED);
	}

	@Test
	void createRejectsOverlappingReservation() {
		ResourceReservation existing = ResourceReservation.builder()
				.id(2L)
				.resourceId(20L)
				.startDateTime(START.plusHours(1))
				.endDateTime(END.plusHours(1))
				.status(ReservationStatus.CONFIRMED)
				.build();
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(availableResource()));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of(existing));

		assertThatThrownBy(() -> service.create(request()))
				.isInstanceOf(ConflictException.class);
	}

	@Test
	void cancelReleasesResourceWhenNoActiveReservationRemains() {
		Resource resource = availableResource();
		resource.changeStatus(ResourceStatus.RESERVED);
		ResourceReservation confirmed = ResourceReservation.builder()
				.id(1L)
				.interventionId(10L)
				.resourceId(20L)
				.startDateTime(START)
				.endDateTime(END)
				.status(ReservationStatus.CONFIRMED)
				.build();
		when(reservationRepository.findById(1L)).thenReturn(Optional.of(confirmed));
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(resource));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of());
		when(reservationRepository.save(any(ResourceReservation.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ResourceReservation cancelled = service.cancel(1L);

		assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
		assertThat(resource.getStatus()).isEqualTo(ResourceStatus.AVAILABLE);
	}
}
