package com.civia.resourcelogisticsservice.service;

import java.util.List;

import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;

public interface IResourceService {

	List<Resource> getAllResources();

	Resource addResource(Resource resource);

	Resource updateResource(Resource resource);

	Resource getResource(Long idResource);

	void deleteResource(Long idResource);

	Resource changeStatus(Long idResource, ResourceStatus status);

	boolean isResourceAvailable(Long idResource);
}
