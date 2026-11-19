package com.civia.citizenengagementservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostConfirmationResponse {

    private Long postId;
    private Long userId;
}