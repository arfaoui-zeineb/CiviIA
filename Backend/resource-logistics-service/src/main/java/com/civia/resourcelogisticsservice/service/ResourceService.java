package com.civia.resourcelogisticsservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.civia.resourcelogisticsservice.domain.model.Resource;
import com.civia.resourcelogisticsservice.domain.model.ResourceStatus;
import com.civia.resourcelogisticsservice.domain.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.exception.ResourceNotFoundException;
import com.civia.resourcelogisticsservice.web.dto.ResourceRequest;

@Service
@Transactional(readOnly = true)
public class ResourceService {

	private final ResourceRepository resourceRepository;

	public ResourceService(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	public List<Resource> findAll() {
		return resourceRepository.findAll();
	}

	public Resource findById(Long id) {
		return resourceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Resource " + id + " not found"));
	}

	@Transactional
	public Resource create(ResourceRequest request) {
		Resource resource = Resource.builder()
				.name(request.name())
				.type(request.type())
				.status(request.status() != null ? request.status() : ResourceStatus.AVAILABLE)
				.location(request.location())
				.build();
		return resourceRepository.save(resource);
	}

	@Transactional
	public Resource update(Long id, ResourceRequest request) {
		Resource resource = findById(id);
		resource.setName(request.name());
		resource.setType(request.type());
		resource.setStatus(request.status() != null ? request.status() : resource.getStatus());
		resource.setLocation(request.location());
		return resourceRepository.save(resource);
	}

	@Transactional
	public void delete(Long id) {
		resourceRepository.delete(findById(id));
	}

	/** changeStatus() : modifie le statut d'une ressource. */
	@Transactional
	public Resource changeStatus(Long id, ResourceStatus status) {
		Resource resource = findById(id);
		resource.changeStatus(status);
		return resourceRepository.save(resource);
	}

	public boolean isAvailable(Long id) {
		return findById(id).isAvailable();
	}
}
