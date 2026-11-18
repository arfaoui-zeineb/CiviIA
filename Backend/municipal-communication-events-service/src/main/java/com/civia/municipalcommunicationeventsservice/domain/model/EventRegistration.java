package com.civia.municipalcommunicationeventsservice.domain.model;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.exception.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Inscription d'un utilisateur a un evenement (§ 4.6). */
@Entity
@Table(name = "event_registrations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long eventId;

	@Column(nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private RegistrationStatus status = RegistrationStatus.REGISTERED;

	@Column(nullable = false)
	private LocalDateTime registeredAt;

	private LocalDateTime cancelledAt;

	@PrePersist
	void setRegisteredAtIfMissing() {
		if (registeredAt == null) {
			registeredAt = LocalDateTime.now();
		}
	}

	public void cancel() {
		if (status == RegistrationStatus.CANCELLED) {
			throw new BusinessRuleException("Registration " + id + " is already cancelled");
		}
		this.status = RegistrationStatus.CANCELLED;
		this.cancelledAt = LocalDateTime.now();
	}
}
