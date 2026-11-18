package com.civia.resourcelogisticsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.resourcelogisticsservice.domain.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
