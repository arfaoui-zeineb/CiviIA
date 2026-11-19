package com.civia.resourcelogisticsservice.repository;

import com.civia.resourcelogisticsservice.domain.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
}
