package com.civia.municipalcommunicationeventsservice.domain.model;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.exception.BusinessRuleException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Evenement local organise par la municipalite (§ 4.6). */
@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long categoryId;
	private Long venueId;
	private Long departmentId;

	@NotBlank
	@Column(nullable = false)
	private String title;

	@Column(length = 10000)
	private String description;

	@Column(nullable = false)
	private LocalDateTime startDate;

	@Column(nullable = false)
	private LocalDateTime endDate;

	@Min(1)
	@Column(nullable = false)
	private int capacity;

	@Builder.Default
	@Column(nullable = false)
	private int registeredCount = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private EventStatus status = EventStatus.DRAFT;

	/** Verrou optimiste : decremente atomique du compteur (§ 4.6). */
	@Version
	private long version;

	@AssertTrue(message = "endDate must be after startDate")
	@JsonIgnore
	public boolean isValidPeriod() {
		return startDate != null && endDate != null && startDate.isBefore(endDate);
	}

	public boolean isFull() {
		return registeredCount >= capacity;
	}

	/** Capacite d'un evenement lie a un lieu : ne peut pas depasser celle du lieu. */
	public void validateCapacityAgainstVenue(Venue venue) {
		if (venue != null && capacity > venue.getCapacity()) {
			throw new BusinessRuleException("Event capacity cannot exceed venue capacity");
		}
	}

	public void open() {
		if (status != EventStatus.DRAFT && status != EventStatus.FULL) {
			throw new BusinessRuleException("Only a DRAFT or FULL event can be opened");
		}
		this.status = EventStatus.OPEN;
	}

	public void cancel() {
		this.status = EventStatus.CANCELLED;
	}
}
