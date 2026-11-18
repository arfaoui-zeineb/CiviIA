package com.civia.resourcelogisticsservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.service.IResourceService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
public class ResourceController {

	private final IResourceService resourceService;

	@PostMapping
	public Resource addResource(@RequestBody Resource resource) {
		return resourceService.addResource(resource);
	}

	@GetMapping
	public List<Resource> retrieveAllResources() {
		return resourceService.retrieveAllResources();
	}

	@GetMapping("/{id}")
	public Resource retrieveResource(@PathVariable Long id) {
		return resourceService.retrieveResource(id);
	}

	@PutMapping("/{id}")
	public Resource updateResource(@PathVariable Long id, @RequestBody Resource resource) {
		resource.setId(id);
		return resourceService.updateResource(resource);
	}

	@PatchMapping("/{id}/status")
	public Resource changeStatus(@PathVariable Long id, @RequestParam ResourceStatus status) {
		return resourceService.changeStatus(id, status);
	}

	@DeleteMapping("/{id}")
	public void removeResource(@PathVariable Long id) {
		resourceService.removeResource(id);
	}

	@GetMapping("/{id}/availability")
	public boolean isResourceAvailable(@PathVariable Long id) {
		return resourceService.isResourceAvailable(id);
	}
}
