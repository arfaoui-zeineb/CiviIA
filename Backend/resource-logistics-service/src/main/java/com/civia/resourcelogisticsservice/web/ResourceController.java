package com.civia.resourcelogisticsservice.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.civia.resourcelogisticsservice.domain.model.ResourceStatus;
import com.civia.resourcelogisticsservice.service.ResourceService;
import com.civia.resourcelogisticsservice.web.dto.AvailabilityResponse;
import com.civia.resourcelogisticsservice.web.dto.ResourceRequest;
import com.civia.resourcelogisticsservice.web.dto.ResourceResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

	private final ResourceService resourceService;

	public ResourceController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@GetMapping
	public List<ResourceResponse> findAll() {
		return resourceService.findAll().stream().map(ResourceResponse::from).toList();
	}

	@GetMapping("/{id}")
	public ResourceResponse findById(@PathVariable Long id) {
		return ResourceResponse.from(resourceService.findById(id));
	}

	@PostMapping
	public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest request) {
		ResourceResponse created = ResourceResponse.from(resourceService.create(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceRequest request) {
		return ResourceResponse.from(resourceService.update(id, request));
	}

	@PatchMapping("/{id}/status")
	public ResourceResponse changeStatus(@PathVariable Long id, @RequestParam ResourceStatus status) {
		return ResourceResponse.from(resourceService.changeStatus(id, status));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		resourceService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/availability")
	public AvailabilityResponse isAvailable(@PathVariable Long id) {
		return new AvailabilityResponse(resourceService.isAvailable(id));
	}
}
