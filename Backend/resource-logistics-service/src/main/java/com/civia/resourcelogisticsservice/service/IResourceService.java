package com.civia.resourcelogisticsservice.service;

import java.util.List;

import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;

public interface IResourceService {

	List<Resource> retrieveAllResources();

	Resource addResource(Resource resource);

	Resource updateResource(Resource resource);

	Resource retrieveResource(Long idResource);

	void removeResource(Long idResource);

	Resource changeStatus(Long idResource, ResourceStatus status);

	boolean isResourceAvailable(Long idResource);
}
