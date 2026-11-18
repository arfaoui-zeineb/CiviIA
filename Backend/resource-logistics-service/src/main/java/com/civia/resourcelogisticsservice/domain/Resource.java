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

/**
 * Ressource materielle (vehicule, equipement, outil). Ce service en est le seul
 * proprietaire (dossier technique § 8.1).
 */
@Entity
@Table(name = "resources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ResourceType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private ResourceStatus status = ResourceStatus.AVAILABLE;

	private String location;

	public boolean isAvailable() {
		return status == ResourceStatus.AVAILABLE;
	}

	public void changeStatus(ResourceStatus newStatus) {
		this.status = newStatus;
	}
}
