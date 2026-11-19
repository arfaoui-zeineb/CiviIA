package com.civia.citizenengagementservice.service;

import com.civia.citizenengagementservice.dto.PostConfirmationRequest;
import com.civia.citizenengagementservice.dto.PostConfirmationResponse;
import com.civia.citizenengagementservice.entities.Post;
import com.civia.citizenengagementservice.entities.PostConfirmation;
import com.civia.citizenengagementservice.entities.PostConfirmationId;
import com.civia.citizenengagementservice.repository.PostConfirmationRepository;
import com.civia.citizenengagementservice.repository.PostRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostConfirmationService {

    private final PostConfirmationRepository confirmationRepository;
    private final PostRepository postRepository;

    public PostConfirmationService(
            PostConfirmationRepository confirmationRepository,
            PostRepository postRepository) {
        this.confirmationRepository = confirmationRepository;
        this.postRepository = postRepository;
    }

    // Synchronize confirmationCount in Post
    private void updatePostConfirmationCount(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setConfirmationCount(
                (int) confirmationRepository.countByIdPostId(postId)
        );

        postRepository.save(post);
    }

    // 1. Create a confirmation
    public PostConfirmationResponse createConfirmation(
            PostConfirmationRequest request) {

        PostConfirmationId id = new PostConfirmationId(
                request.getPostId(),
                request.getUserId()
        );

        PostConfirmation confirmation = new PostConfirmation(id);

        PostConfirmation saved = confirmationRepository.save(confirmation);

        updatePostConfirmationCount(request.getPostId());

        return mapToResponse(saved);
    }

    // 2. Get all confirmations
    public List<PostConfirmationResponse> getAllConfirmations() {

        return confirmationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get one confirmation
    public PostConfirmationResponse getConfirmation(
            Long postId, Long userId) {

        PostConfirmationId id = new PostConfirmationId(postId, userId);

        PostConfirmation confirmation = confirmationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Confirmation not found"));

        return mapToResponse(confirmation);
    }

    // 4. Get confirmations by Post ID
    public List<PostConfirmationResponse> getConfirmationsByPostId(
            Long postId) {

        return confirmationRepository.findByIdPostId(postId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 5. Delete a confirmation
    public void deleteConfirmation(Long postId, Long userId) {

        PostConfirmationId id = new PostConfirmationId(postId, userId);

        PostConfirmation confirmation = confirmationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Confirmation not found"));

        confirmationRepository.delete(confirmation);

        updatePostConfirmationCount(postId);
    }

    // Convert Entity to Response DTO
    private PostConfirmationResponse mapToResponse(
            PostConfirmation confirmation) {

        return new PostConfirmationResponse(
                confirmation.getId().getPostId(),
                confirmation.getId().getUserId()
        );
    }
}