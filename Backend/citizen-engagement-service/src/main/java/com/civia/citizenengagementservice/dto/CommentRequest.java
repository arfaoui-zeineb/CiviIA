package com.civia.citizenengagementservice.dto;

import com.civia.citizenengagementservice.enums.CommentType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    private Long postId;
    private Long parentCommentId;
    private Long userId;
    private CommentType type;
    private Integer rating;
    private String content;
}