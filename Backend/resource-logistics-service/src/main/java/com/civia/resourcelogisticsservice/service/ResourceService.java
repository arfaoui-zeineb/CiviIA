package com.civia.resourcelogisticsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.exception.ResourceNotFoundException;
import com.civia.resourcelogisticsservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceService implements IResourceService {

	private final ResourceRepository resourceRepository;

	@Override
	public List<Resource> retrieveAllResources() {
		return resourceRepository.findAll();
	}

	@Override
	@Transactional
	public Resource addResource(Resource resource) {
		if (resource.getStatus() == null) {
			resource.setStatus(ResourceStatus.AVAILABLE);
		}
		return resourceRepository.save(resource);
	}

	@Override
	@Transactional
	public Resource updateResource(Resource resource) {
		retrieveResource(resource.getId());
		return resourceRepository.save(resource);
	}

	@Override
	public Resource retrieveResource(Long idResource) {
		return resourceRepository.findById(idResource)
				.orElseThrow(() -> new ResourceNotFoundException("Resource " + idResource + " not found"));
	}

	@Override
	@Transactional
	public void removeResource(Long idResource) {
		resourceRepository.deleteById(idResource);
	}

	@Override
	@Transactional
	public Resource changeStatus(Long idResource, ResourceStatus status) {
		Resource resource = retrieveResource(idResource);
		resource.changeStatus(status);
		return resourceRepository.save(resource);
	}

	@Override
	public boolean isResourceAvailable(Long idResource) {
		return retrieveResource(idResource).isAvailable();
	}
}
