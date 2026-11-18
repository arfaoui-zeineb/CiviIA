package com.civia.resourcelogisticsservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Equipe mobilisable pour une intervention (dossier technique § 4.5). */
@Entity
@Table(name = "teams")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	private String specialty;

	private String zone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private TeamStatus status = TeamStatus.AVAILABLE;

	/** Une equipe n'est disponible que si son statut l'autorise. */
	public boolean isAvailable() {
		return status == TeamStatus.AVAILABLE;
	}
}
