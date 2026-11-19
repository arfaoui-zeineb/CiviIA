package com.civia.citizenengagementservice.repository;

import com.civia.citizenengagementservice.entities.PostConfirmation;
import com.civia.citizenengagementservice.entities.PostConfirmationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostConfirmationRepository
        extends JpaRepository<PostConfirmation, PostConfirmationId> {

    List<PostConfirmation> findByIdPostId(Long postId);

    long countByIdPostId(Long postId);
}