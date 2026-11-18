package com.civicpulse.communication.dto.response;

import com.civicpulse.communication.domain.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private RegistrationStatus status;
    private OffsetDateTime registeredAt;
    private OffsetDateTime cancelledAt;
}

