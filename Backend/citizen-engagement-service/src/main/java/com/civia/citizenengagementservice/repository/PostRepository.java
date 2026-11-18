package com.civia.citizenengagementservice.repository;

import com.civia.citizenengagementservice.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIncidentId(Long incidentId);

    boolean existsByIncidentId(Long incidentId);
}