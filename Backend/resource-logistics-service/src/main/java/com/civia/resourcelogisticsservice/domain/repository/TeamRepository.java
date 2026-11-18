package com.civia.resourcelogisticsservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.resourcelogisticsservice.domain.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
