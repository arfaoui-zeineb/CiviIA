package com.civia.citizenengagementservice.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSubscriptionId implements Serializable {

    private Long postId;
    private Long userId;
}