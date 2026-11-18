package com.civia.municipalcommunicationeventsservice.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/** Fichier joint a une annonce (§ 4.6). */
@Entity
@Table(name = "attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long announcementId;

	@NotBlank
	@Column(nullable = false)
	private String fileName;

	@NotBlank
	@Column(nullable = false)
	private String fileUrl;

	private String type;

	private Long sizeBytes;

	private LocalDateTime uploadedAt;
}
