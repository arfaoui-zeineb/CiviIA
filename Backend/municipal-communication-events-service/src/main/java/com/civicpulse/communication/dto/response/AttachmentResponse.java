package com.civicpulse.communication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private Long announcementId;
    private String fileName;
    private String fileUrl;
    private String type;
    private Long sizeBytes;
    private OffsetDateTime uploadedAt;
}

