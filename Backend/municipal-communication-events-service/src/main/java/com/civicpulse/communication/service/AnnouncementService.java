package com.civicpulse.communication.service;

import com.civicpulse.communication.domain.enums.AnnouncementPriority;
import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import com.civicpulse.communication.dto.request.AnnouncementRequest;
import com.civicpulse.communication.dto.response.AnnouncementResponse;
import com.civicpulse.communication.dto.response.AttachmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse createAnnouncement(AnnouncementRequest request);
    AnnouncementResponse getAnnouncementById(Long id);
    Page<AnnouncementResponse> getAllAnnouncements(Long categoryId, Long departmentId,
                                                   AnnouncementStatus status, AnnouncementPriority priority,
                                                   Pageable pageable);
    AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request);
    void deleteAnnouncement(Long id);

    // Dedicated state transitions
    AnnouncementResponse publishAnnouncement(Long id);
    AnnouncementResponse archiveAnnouncement(Long id);

    // Attachments
    AttachmentResponse addAttachment(Long announcementId, MultipartFile file);
    List<AttachmentResponse> getAttachments(Long announcementId);
    void deleteAttachment(Long announcementId, Long attachmentId);

    // AlertRaised integration draft creation
    AnnouncementResponse createDraftFromAlert(String alertId, String title, String content, Long departmentId, Long categoryId);
}

