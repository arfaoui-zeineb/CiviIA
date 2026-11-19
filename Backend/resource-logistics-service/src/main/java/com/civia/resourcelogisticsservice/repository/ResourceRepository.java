package com.civia.resourcelogisticsservice.repository;

import com.civia.resourcelogisticsservice.domain.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {
}
