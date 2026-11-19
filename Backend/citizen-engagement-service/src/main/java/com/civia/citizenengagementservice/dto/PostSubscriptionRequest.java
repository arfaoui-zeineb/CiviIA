package com.civia.citizenengagementservice.dto;

import com.civia.citizenengagementservice.enums.Channel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSubscriptionRequest {

    private Long postId;
    private Long userId;
    private Channel channel;
    private boolean active;
}