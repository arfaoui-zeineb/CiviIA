package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface IResourceService {

    List<Resource> retrieveAllResources();

    Resource addResource(Resource resource);

    Resource updateResource(Resource resource);

    Resource retrieveResource(Long idResource);

    void removeResource(Long idResource);

    boolean isResourceAvailable(Long idResource);

    boolean isResourceAvailable(Long idResource, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Resource changeResourceStatus(Long idResource, ResourceStatus status);
}
