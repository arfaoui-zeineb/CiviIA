package com.civia.citizenengagementservice.dto;

import com.civia.citizenengagementservice.enums.CommentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long parentCommentId;
    private Long userId;
    private CommentType type;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}