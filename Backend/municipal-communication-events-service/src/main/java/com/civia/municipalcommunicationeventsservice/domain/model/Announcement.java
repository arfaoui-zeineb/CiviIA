package com.civia.municipalcommunicationeventsservice.domain.model;

import java.time.LocalDateTime;

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

/** Annonce officielle publiee par un departement (§ 4.6). */
@Entity
@Table(name = "announcements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long categoryId;
	private Long departmentId;
	private Long eventId;

	@NotBlank
	@Column(nullable = false)
	private String title;

	@Column(length = 10000)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AnnouncementStatus status = AnnouncementStatus.DRAFT;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AnnouncementPriority priority = AnnouncementPriority.NORMAL;

	private LocalDateTime publishedAt;
	private LocalDateTime expiresAt;

	public void publish() {
		this.status = AnnouncementStatus.PUBLISHED;
		this.publishedAt = LocalDateTime.now();
	}

	public void expire() {
		this.status = AnnouncementStatus.EXPIRED;
	}

	public void archive() {
		this.status = AnnouncementStatus.ARCHIVED;
	}
}
