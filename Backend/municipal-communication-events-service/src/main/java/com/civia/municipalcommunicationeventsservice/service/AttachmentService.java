package com.civia.municipalcommunicationeventsservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.municipalcommunicationeventsservice.domain.model.Attachment;
import com.civia.municipalcommunicationeventsservice.domain.repository.AnnouncementRepository;
import com.civia.municipalcommunicationeventsservice.domain.repository.AttachmentRepository;
import com.civia.municipalcommunicationeventsservice.exception.ResourceNotFoundException;
import com.civia.municipalcommunicationeventsservice.web.dto.AttachmentRequest;

@Service
@Transactional(readOnly = true)
public class AttachmentService {

	private final AttachmentRepository attachmentRepository;
	private final AnnouncementRepository announcementRepository;

	public AttachmentService(AttachmentRepository attachmentRepository, AnnouncementRepository announcementRepository) {
		this.attachmentRepository = attachmentRepository;
		this.announcementRepository = announcementRepository;
	}

	public List<Attachment> findByAnnouncementId(Long announcementId) {
		return attachmentRepository.findByAnnouncementId(announcementId);
	}

	public Attachment findById(Long id) {
		return attachmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Attachment " + id + " not found"));
	}

	@Transactional
	public Attachment create(AttachmentRequest request) {
		if (!announcementRepository.existsById(request.announcementId())) {
			throw new ResourceNotFoundException("Announcement " + request.announcementId() + " not found");
		}
		Attachment attachment = Attachment.builder()
				.announcementId(request.announcementId())
				.fileName(request.fileName())
				.fileUrl(request.fileUrl())
				.type(request.type())
				.sizeBytes(request.sizeBytes())
				.uploadedAt(LocalDateTime.now())
				.build();
		return attachmentRepository.save(attachment);
	}

	@Transactional
	public void delete(Long id) {
		attachmentRepository.delete(findById(id));
	}
}
