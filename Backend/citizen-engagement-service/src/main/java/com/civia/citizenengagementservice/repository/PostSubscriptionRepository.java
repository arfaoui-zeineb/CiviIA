package com.civia.citizenengagementservice.repository;

import com.civia.citizenengagementservice.entities.PostSubscription;
import com.civia.citizenengagementservice.entities.PostSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostSubscriptionRepository
        extends JpaRepository<PostSubscription, PostSubscriptionId> {

    List<PostSubscription> findByIdPostId(Long postId);
}