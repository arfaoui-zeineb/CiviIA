package com.civicpulse.communication.dto.response;

import com.civicpulse.communication.domain.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long venueId;
    private String venueName;
    private Long departmentId;
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Integer capacity;
    private Integer registeredCount;
    private EventStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

