package com.civia.resourcelogisticsservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceReservation;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.ResourceType;
import com.civia.resourcelogisticsservice.domain.Team;
import com.civia.resourcelogisticsservice.domain.TeamStatus;
import com.civia.resourcelogisticsservice.exception.BusinessRuleException;
import com.civia.resourcelogisticsservice.exception.ConflictException;
import org.junit.jupiter.api.Test;

class ResourceReservationTest {

	private static final LocalDateTime START = LocalDateTime.of(2026, 11, 20, 8, 0);
	private static final LocalDateTime END = LocalDateTime.of(2026, 11, 20, 12, 0);

	private ResourceReservation reservation(LocalDateTime start, LocalDateTime end) {
		return ResourceReservation.builder()
				.id(1L)
				.interventionId(10L)
				.resourceId(20L)
				.startDateTime(start)
				.endDateTime(end)
				.status(ReservationStatus.PENDING)
				.build();
	}

	@Test
	void intervalsThatTouchAreNotOverlapping() {
		ResourceReservation first = reservation(START, END);
		ResourceReservation next = reservation(END, END.plusHours(2));

		assertThat(first.overlaps(next)).isFalse();
		assertThat(next.overlaps(first)).isFalse();
	}

	@Test
	void checkAvailabilityRejectsOverlappingActiveReservation() {
		ResourceReservation candidate = reservation(START.plusHours(1), END.plusHours(1));
		ResourceReservation existing = ResourceReservation.builder()
				.id(2L)
				.resourceId(20L)
				.startDateTime(START)
				.endDateTime(END)
				.status(ReservationStatus.CONFIRMED)
				.build();

		assertThatThrownBy(() -> candidate.checkAvailability(List.of(existing)))
				.isInstanceOf(ConflictException.class);
	}

	@Test
	void checkAvailabilityIgnoresCancelledAndSelfReservations() {
		ResourceReservation candidate = reservation(START, END);
		ResourceReservation cancelled = ResourceReservation.builder()
				.id(3L)
				.resourceId(20L)
				.startDateTime(START)
				.endDateTime(END)
				.status(ReservationStatus.CANCELLED)
				.build();

		candidate.checkAvailability(List.of(cancelled, candidate));
	}

	@Test
	void reserveConfirmsAndMarksResourceAsReserved() {
		Resource resource = Resource.builder()
				.id(20L).name("Truck").type(ResourceType.VEHICLE)
				.status(ResourceStatus.AVAILABLE).build();
		ResourceReservation candidate = reservation(START, END);

		candidate.reserve(resource);

		assertThat(candidate.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(resource.getStatus()).isEqualTo(ResourceStatus.RESERVED);
	}

	@Test
	void reserveIsRejectedWhenResourceIsNotAvailable() {
		Resource resource = Resource.builder()
				.id(20L).name("Truck").type(ResourceType.VEHICLE)
				.status(ResourceStatus.MAINTENANCE).build();
		ResourceReservation candidate = reservation(START, END);

		assertThatThrownBy(() -> candidate.reserve(resource))
				.isInstanceOf(BusinessRuleException.class);
	}

	@Test
	void cancelClosesTheReservation() {
		ResourceReservation candidate = reservation(START, END);
		candidate.reserve(Resource.builder().id(20L).type(ResourceType.TOOL)
				.status(ResourceStatus.AVAILABLE).build());

		candidate.cancel();

		assertThat(candidate.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
		assertThatThrownBy(candidate::cancel).isInstanceOf(BusinessRuleException.class);
	}

	@Test
	void teamIsAvailableOnlyWhenStatusAllowsIt() {
		Team available = Team.builder().id(1L).name("Team A").status(TeamStatus.AVAILABLE).build();
		Team maintenance = Team.builder().id(2L).name("Team B").status(TeamStatus.MAINTENANCE).build();

		assertThat(available.isAvailable()).isTrue();
		assertThat(maintenance.isAvailable()).isFalse();
	}
}
