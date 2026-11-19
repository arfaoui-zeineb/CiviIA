package com.civia.citizenengagementservice.service;

import com.civia.citizenengagementservice.dto.PostSubscriptionRequest;
import com.civia.citizenengagementservice.dto.PostSubscriptionResponse;
import com.civia.citizenengagementservice.entities.PostSubscription;
import com.civia.citizenengagementservice.entities.PostSubscriptionId;
import com.civia.citizenengagementservice.repository.PostSubscriptionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostSubscriptionService {

    private final PostSubscriptionRepository subscriptionRepository;

    public PostSubscriptionService(
            PostSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // 1. Create
    public PostSubscriptionResponse createSubscription(
            PostSubscriptionRequest request) {

        PostSubscriptionId id = new PostSubscriptionId(
                request.getPostId(),
                request.getUserId()
        );

        PostSubscription subscription = new PostSubscription(
                id,
                request.getChannel(),
                request.isActive()
        );

        PostSubscription saved = subscriptionRepository.save(subscription);

        return mapToResponse(saved);
    }

    // 2. Get all
    public List<PostSubscriptionResponse> getAllSubscriptions() {

        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get one by composite ID
    public PostSubscriptionResponse getSubscription(
            Long postId, Long userId) {

        PostSubscriptionId id = new PostSubscriptionId(postId, userId);

        PostSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Subscription not found"));

        return mapToResponse(subscription);
    }

    // 4. Get subscriptions by Post ID
    public List<PostSubscriptionResponse> getSubscriptionsByPostId(
            Long postId) {

        return subscriptionRepository.findByIdPostId(postId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 5. Update
    public PostSubscriptionResponse updateSubscription(
            Long postId,
            Long userId,
            PostSubscriptionRequest request) {

        PostSubscriptionId id = new PostSubscriptionId(postId, userId);

        PostSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Subscription not found"));

        subscription.setChannel(request.getChannel());
        subscription.setActive(request.isActive());

        PostSubscription updated = subscriptionRepository.save(subscription);

        return mapToResponse(updated);
    }

    // 6. Delete
    public void deleteSubscription(Long postId, Long userId) {

        PostSubscriptionId id = new PostSubscriptionId(postId, userId);

        PostSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Subscription not found"));

        subscriptionRepository.delete(subscription);
    }

    // Convert Entity to Response DTO
    private PostSubscriptionResponse mapToResponse(
            PostSubscription subscription) {

        return new PostSubscriptionResponse(
                subscription.getId().getPostId(),
                subscription.getId().getUserId(),
                subscription.getChannel(),
                subscription.isActive()
        );
    }
}