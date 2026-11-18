package com.civia.municipalcommunicationeventsservice.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.civia.municipalcommunicationeventsservice.service.AnnouncementService;
import com.civia.municipalcommunicationeventsservice.service.AttachmentService;
import com.civia.municipalcommunicationeventsservice.web.dto.AnnouncementRequest;
import com.civia.municipalcommunicationeventsservice.web.dto.AnnouncementResponse;
import com.civia.municipalcommunicationeventsservice.web.dto.AttachmentRequest;
import com.civia.municipalcommunicationeventsservice.web.dto.AttachmentResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

	private final AnnouncementService announcementService;
	private final AttachmentService attachmentService;

	public AnnouncementController(AnnouncementService announcementService, AttachmentService attachmentService) {
		this.announcementService = announcementService;
		this.attachmentService = attachmentService;
	}

	@GetMapping
	public List<AnnouncementResponse> findAll() {
		return announcementService.findAll().stream().map(AnnouncementResponse::from).toList();
	}

	@GetMapping("/{id}")
	public AnnouncementResponse findById(@PathVariable Long id) {
		return AnnouncementResponse.from(announcementService.findById(id));
	}

	@PostMapping
	public ResponseEntity<AnnouncementResponse> create(@Valid @RequestBody AnnouncementRequest request) {
		AnnouncementResponse created = AnnouncementResponse.from(announcementService.create(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public AnnouncementResponse update(@PathVariable Long id, @Valid @RequestBody AnnouncementRequest request) {
		return AnnouncementResponse.from(announcementService.update(id, request));
	}

	@PostMapping("/{id}/publish")
	public AnnouncementResponse publish(@PathVariable Long id) {
		return AnnouncementResponse.from(announcementService.publish(id));
	}

	/** DELETE archive l'annonce au lieu de la supprimer physiquement. */
	@DeleteMapping("/{id}")
	public AnnouncementResponse archive(@PathVariable Long id) {
		return AnnouncementResponse.from(announcementService.archive(id));
	}

	@GetMapping("/{id}/attachments")
	public List<AttachmentResponse> findAttachments(@PathVariable Long id) {
		return attachmentService.findByAnnouncementId(id).stream().map(AttachmentResponse::from).toList();
	}

	@PostMapping("/{id}/attachments")
	public ResponseEntity<AttachmentResponse> addAttachment(@PathVariable Long id,
			@Valid @RequestBody AttachmentRequest request) {
		AttachmentRequest withAnnouncement = new AttachmentRequest(id, request.fileName(), request.fileUrl(),
				request.type(), request.sizeBytes());
		AttachmentResponse created = AttachmentResponse.from(attachmentService.create(withAnnouncement));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}
}
