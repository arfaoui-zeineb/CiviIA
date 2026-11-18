package com.civia.resourcelogisticsservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.resourcelogisticsservice.domain.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
