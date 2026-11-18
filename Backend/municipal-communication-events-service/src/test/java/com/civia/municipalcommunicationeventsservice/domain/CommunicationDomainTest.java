package com.civia.municipalcommunicationeventsservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.Announcement;
import com.civia.municipalcommunicationeventsservice.domain.model.AnnouncementStatus;
import com.civia.municipalcommunicationeventsservice.domain.model.Event;
import com.civia.municipalcommunicationeventsservice.domain.model.EventRegistration;
import com.civia.municipalcommunicationeventsservice.domain.model.EventStatus;
import com.civia.municipalcommunicationeventsservice.domain.model.RegistrationStatus;
import com.civia.municipalcommunicationeventsservice.domain.model.Venue;
import com.civia.municipalcommunicationeventsservice.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

class CommunicationDomainTest {

	private static final LocalDateTime START = LocalDateTime.of(2026, 12, 1, 10, 0);
	private static final LocalDateTime END = LocalDateTime.of(2026, 12, 1, 12, 0);

	private Event event(int capacity) {
		return Event.builder()
				.id(1L)
				.title("Forum")
				.startDate(START)
				.endDate(END)
				.capacity(capacity)
				.status(EventStatus.OPEN)
				.build();
	}

	@Test
	void eventPeriodMustBeValid() {
		assertThat(event(10).isValidPeriod()).isTrue();
		Event invalid = Event.builder().title("Bad").startDate(END).endDate(START).capacity(5).build();
		assertThat(invalid.isValidPeriod()).isFalse();
	}

	@Test
	void eventCapacityCannotExceedVenueCapacity() {
		Venue venue = Venue.builder().id(2L).name("Hall").capacity(50).build();

		assertThatThrownBy(() -> event(60).validateCapacityAgainstVenue(venue))
				.isInstanceOf(BusinessRuleException.class);
		event(50).validateCapacityAgainstVenue(venue);
	}

	@Test
	void eventBecomesFullWhenCapacityIsReached() {
		Event event = event(1);
		assertThat(event.isFull()).isFalse();
		event.setRegisteredCount(1);
		assertThat(event.isFull()).isTrue();
	}

	@Test
	void announcementLifecycle() {
		Announcement announcement = Announcement.builder().id(3L).title("Info").build();
		announcement.publish();
		assertThat(announcement.getStatus()).isEqualTo(AnnouncementStatus.PUBLISHED);
		assertThat(announcement.getPublishedAt()).isNotNull();
		announcement.expire();
		assertThat(announcement.getStatus()).isEqualTo(AnnouncementStatus.EXPIRED);
		announcement.archive();
		assertThat(announcement.getStatus()).isEqualTo(AnnouncementStatus.ARCHIVED);
	}

	@Test
	void registrationCancellationSetsDateAndStatus() {
		EventRegistration registration = EventRegistration.builder()
				.id(4L).eventId(1L).userId(7L).status(RegistrationStatus.REGISTERED).build();

		registration.cancel();

		assertThat(registration.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
		assertThat(registration.getCancelledAt()).isNotNull();
		assertThatThrownBy(registration::cancel).isInstanceOf(BusinessRuleException.class);
	}
}
