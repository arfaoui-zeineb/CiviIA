
package com.civia.citizenengagementservice.controller;

import com.civia.citizenengagementservice.dto.PostRequest;
import com.civia.citizenengagementservice.dto.PostResponse;
import com.civia.citizenengagementservice.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request) {

        PostResponse response = postService.createPost(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {

        return ResponseEntity.ok(postService.getAllPosts());
    }

    // Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id) {

        return ResponseEntity.ok(postService.getPostById(id));
    }

    // Get post by incident ID
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<PostResponse> getPostByIncidentId(
            @PathVariable Long incidentId) {

        return ResponseEntity.ok(
                postService.getPostByIncidentId(incidentId)
        );
    }

    // Update a post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequest request) {

        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {

        postService.deletePost(id);

        return ResponseEntity.noContent().build();
    }

}
