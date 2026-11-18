package com.civicpulse.communication.dto.request;

import com.civicpulse.communication.domain.enums.AnnouncementPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    @NotNull(message = "Department ID is mandatory")
    private Long departmentId;

    private Long eventId; // Nullable

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @Builder.Default
    private AnnouncementPriority priority = AnnouncementPriority.NORMAL;

    private OffsetDateTime expiresAt;
}

