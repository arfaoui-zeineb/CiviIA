package com.civia.municipalcommunicationeventsservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttachmentRequest(
		@NotNull Long announcementId,
		@NotBlank String fileName,
		@NotBlank String fileUrl,
		String type,
		Long sizeBytes) {
}
