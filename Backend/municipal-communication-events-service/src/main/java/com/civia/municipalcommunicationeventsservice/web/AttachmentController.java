package com.civia.municipalcommunicationeventsservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.civia.municipalcommunicationeventsservice.service.AttachmentService;
import com.civia.municipalcommunicationeventsservice.web.dto.AttachmentResponse;

@RestController
@RequestMapping("/api/announcement-attachments")
public class AttachmentController {

	private final AttachmentService attachmentService;

	public AttachmentController(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@GetMapping("/{id}")
	public AttachmentResponse findById(@PathVariable Long id) {
		return AttachmentResponse.from(attachmentService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		attachmentService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
