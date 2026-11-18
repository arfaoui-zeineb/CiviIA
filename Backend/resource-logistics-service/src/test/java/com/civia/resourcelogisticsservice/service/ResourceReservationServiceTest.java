package com.civia.resourcelogisticsservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.ResourceType;
import com.civia.resourcelogisticsservice.exception.BusinessRuleException;
import com.civia.resourcelogisticsservice.exception.ConflictException;
import com.civia.resourcelogisticsservice.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import com.civia.resourcelogisticsservice.repository.TeamRepository;
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

	private Resource availableResource() {
		return Resource.builder()
				.id(20L).name("Truck").type(ResourceType.VEHICLE)
				.status(ResourceStatus.AVAILABLE).build();
	}

	private ResourceReservation newReservation() {
		return ResourceReservation.builder()
				.interventionId(10L)
				.resourceId(20L)
				.startDateTime(START)
				.endDateTime(END)
				.build();
	}

	@Test
	void addReservationConfirmsAndReservesResource() {
		Resource resource = availableResource();
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(resource));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of());
		when(reservationRepository.save(any(ResourceReservation.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ResourceReservation created = service.addReservation(newReservation());

		assertThat(created.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(resource.getStatus()).isEqualTo(ResourceStatus.RESERVED);
	}

	@Test
	void addReservationRejectsOverlap() {
		ResourceReservation existing = ResourceReservation.builder()
				.id(2L).resourceId(20L)
				.startDateTime(START.plusHours(1)).endDateTime(END.plusHours(1))
				.status(ReservationStatus.CONFIRMED).build();
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(availableResource()));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of(existing));

		assertThatThrownBy(() -> service.addReservation(newReservation()))
				.isInstanceOf(ConflictException.class);
	}

	@Test
	void updateReservationKeepsStatusAndAppliesNewPeriod() {
		ResourceReservation existing = ResourceReservation.builder()
				.id(1L).interventionId(10L).resourceId(20L)
				.startDateTime(START).endDateTime(END)
				.status(ReservationStatus.CONFIRMED).build();
		ResourceReservation changes = ResourceReservation.builder()
				.id(1L).interventionId(11L).resourceId(20L)
				.startDateTime(START.plusDays(1)).endDateTime(END.plusDays(1)).build();
		when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(availableResource()));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of());
		when(reservationRepository.save(any(ResourceReservation.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ResourceReservation updated = service.updateReservation(changes);

		assertThat(updated.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(updated.getInterventionId()).isEqualTo(11L);
		assertThat(updated.getStartDateTime()).isEqualTo(START.plusDays(1));
	}

	@Test
	void updateReservationIsRefusedWhenClosed() {
		ResourceReservation existing = ResourceReservation.builder()
				.id(1L).resourceId(20L).startDateTime(START).endDateTime(END)
				.status(ReservationStatus.CANCELLED).build();
		when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

		ResourceReservation changes = ResourceReservation.builder()
				.id(1L).resourceId(20L).startDateTime(START).endDateTime(END).build();
		assertThatThrownBy(() -> service.updateReservation(changes))
				.isInstanceOf(BusinessRuleException.class);
	}

	@Test
	void cancelReservationReleasesResource() {
		Resource resource = availableResource();
		resource.changeStatus(ResourceStatus.RESERVED);
		ResourceReservation confirmed = ResourceReservation.builder()
				.id(1L).interventionId(10L).resourceId(20L)
				.startDateTime(START).endDateTime(END)
				.status(ReservationStatus.CONFIRMED).build();
		when(reservationRepository.findById(1L)).thenReturn(Optional.of(confirmed));
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(resource));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of());
		when(reservationRepository.save(any(ResourceReservation.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		ResourceReservation cancelled = service.cancelReservation(1L);

		assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
		assertThat(resource.getStatus()).isEqualTo(ResourceStatus.AVAILABLE);
	}

	@Test
	void removeReservationDeletesAndReleasesResource() {
		Resource resource = availableResource();
		resource.changeStatus(ResourceStatus.RESERVED);
		ResourceReservation confirmed = ResourceReservation.builder()
				.id(1L).resourceId(20L).startDateTime(START).endDateTime(END)
				.status(ReservationStatus.CONFIRMED).build();
		when(reservationRepository.findById(1L)).thenReturn(Optional.of(confirmed));
		when(resourceRepository.findById(20L)).thenReturn(Optional.of(resource));
		when(reservationRepository.findByResourceIdAndStatusIn(any(), any())).thenReturn(List.of());

		service.removeReservation(1L);

		verify(reservationRepository).deleteById(1L);
		assertThat(resource.getStatus()).isEqualTo(ResourceStatus.AVAILABLE);
	}
}
