package com.civia.citizenengagementservice.dto;

import com.civia.citizenengagementservice.enums.ComplaintStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {

    private Long id;
    private Long evaluationId;
    private Long userId;
    private String reason;
    private ComplaintStatus status;
    private String resolution;
    private LocalDateTime createdAt;
}