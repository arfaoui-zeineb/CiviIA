package com.civia.citizenengagementservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostConfirmationRequest {

    private Long postId;
    private Long userId;
}