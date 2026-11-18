package com.civia.municipalcommunicationeventsservice.web.dto;

import java.time.LocalDateTime;

import com.civia.municipalcommunicationeventsservice.domain.model.Attachment;

public record AttachmentResponse(
		Long id,
		Long announcementId,
		String fileName,
		String fileUrl,
		String type,
		Long sizeBytes,
		LocalDateTime uploadedAt) {

	public static AttachmentResponse from(Attachment attachment) {
		return new AttachmentResponse(attachment.getId(), attachment.getAnnouncementId(), attachment.getFileName(),
				attachment.getFileUrl(), attachment.getType(), attachment.getSizeBytes(), attachment.getUploadedAt());
	}
}
