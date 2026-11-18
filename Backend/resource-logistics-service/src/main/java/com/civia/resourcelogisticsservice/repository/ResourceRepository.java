package com.civia.resourcelogisticsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.civia.resourcelogisticsservice.domain.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
