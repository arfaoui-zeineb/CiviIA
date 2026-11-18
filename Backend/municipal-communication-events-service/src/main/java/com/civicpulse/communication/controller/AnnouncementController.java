package com.civicpulse.communication.controller;

import com.civicpulse.communication.domain.enums.AnnouncementPriority;
import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import com.civicpulse.communication.dto.request.AnnouncementRequest;
import com.civicpulse.communication.dto.response.AnnouncementResponse;
import com.civicpulse.communication.dto.response.AttachmentResponse;
import com.civicpulse.communication.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Endpoints for managing municipal announcements, publishing, archiving and attachments")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @Operation(summary = "Create a new announcement in DRAFT status")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        AnnouncementResponse response = announcementService.createAnnouncement(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an announcement by ID")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getAnnouncementById(id));
    }

    @GetMapping
    @Operation(summary = "Get announcements with filtering (category, department, status, priority) and pagination")
    public ResponseEntity<Page<AnnouncementResponse>> getAllAnnouncements(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) AnnouncementStatus status,
            @RequestParam(required = false) AnnouncementPriority priority,
            Pageable pageable) {
        return ResponseEntity.ok(announcementService.getAllAnnouncements(categoryId, departmentId, status, priority, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an announcement")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an announcement")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    // --- State Transitions ---

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish an announcement (DRAFT -> PUBLISHED)")
    public ResponseEntity<AnnouncementResponse> publishAnnouncement(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.publishAnnouncement(id));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive an announcement")
    public ResponseEntity<AnnouncementResponse> archiveAnnouncement(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.archiveAnnouncement(id));
    }

    // --- Attachments ---

    @PostMapping(value = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and attach a file to an announcement")
    public ResponseEntity<AttachmentResponse> addAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        AttachmentResponse response = announcementService.addAttachment(id, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{attachmentId}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}/attachments")
    @Operation(summary = "Get all attachments for an announcement")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getAttachments(id));
    }

    @DeleteMapping("/{id}/attachments/{attachmentId}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId) {
        announcementService.deleteAttachment(id, attachmentId);
        return ResponseEntity.noContent().build();
    }
}

