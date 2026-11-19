package com.civia.citizenengagementservice.controller;

import com.civia.citizenengagementservice.dto.PostSubscriptionRequest;
import com.civia.citizenengagementservice.dto.PostSubscriptionResponse;
import com.civia.citizenengagementservice.service.PostSubscriptionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class PostSubscriptionController {

    private final PostSubscriptionService subscriptionService;

    public PostSubscriptionController(
            PostSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Create
    @PostMapping
    public ResponseEntity<PostSubscriptionResponse> createSubscription(
            @RequestBody PostSubscriptionRequest request) {

        return new ResponseEntity<>(
                subscriptionService.createSubscription(request),
                HttpStatus.CREATED
        );
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<PostSubscriptionResponse>> getAllSubscriptions() {

        return ResponseEntity.ok(
                subscriptionService.getAllSubscriptions()
        );
    }

    // Get one
    @GetMapping("/{postId}/{userId}")
    public ResponseEntity<PostSubscriptionResponse> getSubscription(
            @PathVariable Long postId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                subscriptionService.getSubscription(postId, userId)
        );
    }

    // Get subscriptions by Post ID
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostSubscriptionResponse>> getByPostId(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                subscriptionService.getSubscriptionsByPostId(postId)
        );
    }

    // Update
    @PutMapping("/{postId}/{userId}")
    public ResponseEntity<PostSubscriptionResponse> updateSubscription(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestBody PostSubscriptionRequest request) {

        return ResponseEntity.ok(
                subscriptionService.updateSubscription(
                        postId, userId, request
                )
        );
    }

    // Delete
    @DeleteMapping("/{postId}/{userId}")
    public ResponseEntity<Void> deleteSubscription(
            @PathVariable Long postId,
            @PathVariable Long userId) {

        subscriptionService.deleteSubscription(postId, userId);

        return ResponseEntity.noContent().build();
    }
}