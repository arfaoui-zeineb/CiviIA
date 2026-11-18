package com.civia.citizenengagementservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {

    private Long evaluationId;
    private Long userId;
    private String reason;
    private String resolution;
}