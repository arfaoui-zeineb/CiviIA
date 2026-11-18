package com.civia.citizenengagementservice.controller;

import com.civia.citizenengagementservice.dto.CommentRequest;
import com.civia.citizenengagementservice.dto.CommentResponse;
import com.civia.citizenengagementservice.service.CommentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Create
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentRequest request) {

        return new ResponseEntity<>(
                commentService.createComment(request),
                HttpStatus.CREATED
        );
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {

        return ResponseEntity.ok(commentService.getAllComments());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // Get comments by Post ID
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getCommentsByPostId(postId)
        );
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequest request) {

        return ResponseEntity.ok(
                commentService.updateComment(id, request)
        );
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);

        return ResponseEntity.noContent().build();
    }
}