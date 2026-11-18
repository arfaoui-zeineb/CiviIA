package com.civia.citizenengagementservice.entities;

import com.civia.citizenengagementservice.enums.CommentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    private Long parentCommentId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentType type;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
}