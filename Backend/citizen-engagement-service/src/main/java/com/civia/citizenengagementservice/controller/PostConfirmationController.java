package com.civia.citizenengagementservice.controller;

import com.civia.citizenengagementservice.dto.PostConfirmationRequest;
import com.civia.citizenengagementservice.dto.PostConfirmationResponse;
import com.civia.citizenengagementservice.service.PostConfirmationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/confirmations")
public class PostConfirmationController {

    private final PostConfirmationService confirmationService;

    public PostConfirmationController(
            PostConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    // Create
    @PostMapping
    public ResponseEntity<PostConfirmationResponse> createConfirmation(
            @RequestBody PostConfirmationRequest request) {

        return new ResponseEntity<>(
                confirmationService.createConfirmation(request),
                HttpStatus.CREATED
        );
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<PostConfirmationResponse>> getAllConfirmations() {

        return ResponseEntity.ok(
                confirmationService.getAllConfirmations()
        );
    }

    // Get one by composite ID
    @GetMapping("/{postId}/{userId}")
    public ResponseEntity<PostConfirmationResponse> getConfirmation(
            @PathVariable Long postId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                confirmationService.getConfirmation(postId, userId)
        );
    }

    // Get confirmations by Post ID
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostConfirmationResponse>> getByPostId(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                confirmationService.getConfirmationsByPostId(postId)
        );
    }

    // Delete
    @DeleteMapping("/{postId}/{userId}")
    public ResponseEntity<Void> deleteConfirmation(
            @PathVariable Long postId,
            @PathVariable Long userId) {

        confirmationService.deleteConfirmation(postId, userId);

        return ResponseEntity.noContent().build();
    }
}