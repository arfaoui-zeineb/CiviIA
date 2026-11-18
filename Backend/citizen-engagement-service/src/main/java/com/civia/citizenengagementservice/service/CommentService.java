package com.civia.citizenengagementservice.service;

import com.civia.citizenengagementservice.dto.CommentRequest;
import com.civia.citizenengagementservice.dto.CommentResponse;
import com.civia.citizenengagementservice.entities.Comment;
import com.civia.citizenengagementservice.repository.CommentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // 1. Create
    public CommentResponse createComment(CommentRequest request) {

        Comment comment = new Comment();

        comment.setPostId(request.getPostId());
        comment.setParentCommentId(request.getParentCommentId());
        comment.setUserId(request.getUserId());
        comment.setType(request.getType());
        comment.setRating(request.getRating());
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    // 2. Get all
    public List<CommentResponse> getAllComments() {

        return commentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Get by ID
    public CommentResponse getCommentById(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return mapToResponse(comment);
    }

    // 4. Get comments by Post ID
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        return commentRepository.findByPostId(postId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 5. Update
    public CommentResponse updateComment(Long id, CommentRequest request) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setPostId(request.getPostId());
        comment.setParentCommentId(request.getParentCommentId());
        comment.setUserId(request.getUserId());
        comment.setType(request.getType());
        comment.setRating(request.getRating());
        comment.setContent(request.getContent());

        Comment updatedComment = commentRepository.save(comment);

        return mapToResponse(updatedComment);
    }

    // 6. Delete
    public void deleteComment(Long id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
    }

    // Convert Entity to Response DTO
    private CommentResponse mapToResponse(Comment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getUserId(),
                comment.getType(),
                comment.getRating(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}