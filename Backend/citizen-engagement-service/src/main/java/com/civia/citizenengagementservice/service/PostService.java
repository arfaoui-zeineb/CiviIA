
package com.civia.citizenengagementservice.service;

import com.civia.citizenengagementservice.dto.PostRequest;
import com.civia.citizenengagementservice.dto.PostResponse;
import com.civia.citizenengagementservice.entities.Post;
import com.civia.citizenengagementservice.enums.PostStatus;
import com.civia.citizenengagementservice.repository.PostRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 1. Create a new post
    public PostResponse createPost(PostRequest request) {

        // Check if a post already exists for this incident
        if (postRepository.existsByIncidentId(request.getIncidentId())) {
            throw new RuntimeException(
                    "A post already exists for this incident"
            );
        }

        Post post = new Post();

        post.setIncidentId(request.getIncidentId());
        post.setAuthorId(request.getAuthorId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        // Initial status
        post.setStatus(PostStatus.OPEN);

        // Initial confirmation count
        post.setConfirmationCount(0);

        Post savedPost = postRepository.save(post);

        return mapToResponse(savedPost);
    }

    // 2. Get all posts
    public List<PostResponse> getAllPosts() {

        return postRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get a post by its ID
    public PostResponse getPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Post not found")
                );

        return mapToResponse(post);
    }

    // 4. Get a post by incident ID
    public PostResponse getPostByIncidentId(Long incidentId) {

        Post post = postRepository.findByIncidentId(incidentId)
                .orElseThrow(() ->
                        new RuntimeException("Post not found for this incident")
                );

        return mapToResponse(post);
    }

    // Convert Entity into Response DTO
    private PostResponse mapToResponse(Post post) {

        return new PostResponse(
                post.getId(),
                post.getIncidentId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                post.getStatus(),
                post.getConfirmationCount()
        );
    }

    // 5. Update an existing post
    public PostResponse updatePost(Long id, PostRequest request) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check whether another post already uses this incident ID
        if (!post.getIncidentId().equals(request.getIncidentId())
                && postRepository.existsByIncidentId(request.getIncidentId())) {
            throw new RuntimeException("This incident already has a post");
        }

        post.setIncidentId(request.getIncidentId());
        post.setAuthorId(request.getAuthorId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post updatedPost = postRepository.save(post);

        return mapToResponse(updatedPost);
    }

    // 6. Delete a post
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(post);
    }

}
