package com.civia.citizenengagementservice.entities;

import com.civia.citizenengagementservice.enums.Channel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSubscription {

    @EmbeddedId
    private PostSubscriptionId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Column(nullable = false)
    private boolean active;
}