package com.civicpulse.communication.dto.response;

import com.civicpulse.communication.domain.enums.AnnouncementPriority;
import com.civicpulse.communication.domain.enums.AnnouncementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long departmentId;
    private Long eventId;
    private String title;
    private String content;
    private AnnouncementStatus status;
    private AnnouncementPriority priority;
    private OffsetDateTime publishedAt;
    private OffsetDateTime expiresAt;
    private List<AttachmentResponse> attachments;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

