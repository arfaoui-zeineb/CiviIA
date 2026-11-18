package com.civia.municipalcommunicationeventsservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.municipalcommunicationeventsservice.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
