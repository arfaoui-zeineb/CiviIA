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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Resource")
@Tag(name = "Resources", description = "Gestion des ressources materielles")
public class ResourceController {

	private final IResourceService resourceService;

	@PostMapping("/addResource")
	@Operation(summary = "addResource", description = "Creer une ressource")
	public Resource addResource(@RequestBody Resource resource) {
		return resourceService.addResource(resource);
	}

	@GetMapping("/getAllResources")
	@Operation(summary = "getAllResources", description = "Recuperer toutes les ressources")
	public List<Resource> getAllResources() {
		return resourceService.getAllResources();
	}

	@GetMapping("/getResource/{idResource}")
	@Operation(summary = "getResource", description = "Recuperer une ressource par identifiant")
	public Resource getResource(@Parameter(name = "idResource", description = "idResource", required = true)
			@PathVariable("idResource") Long idResource) {
		return resourceService.getResource(idResource);
	}

	@PutMapping("/updateResource")
	@Operation(summary = "updateResource", description = "Remplacer une ressource")
	public Resource updateResource(@RequestBody Resource resource) {
		return resourceService.updateResource(resource);
	}

	@PatchMapping("/changeStatus/{idResource}")
	@Operation(summary = "changeStatus", description = "Changer le statut d'une ressource")
	public Resource changeStatus(
			@Parameter(name = "idResource", description = "idResource", required = true)
			@PathVariable("idResource") Long idResource,
			@RequestParam ResourceStatus status) {
		return resourceService.changeStatus(idResource, status);
	}

	@DeleteMapping("/deleteResource/{idResource}")
	@Operation(summary = "deleteResource", description = "Supprimer une ressource")
	public void deleteResource(@Parameter(name = "idResource", description = "idResource", required = true)
			@PathVariable("idResource") Long idResource) {
		resourceService.deleteResource(idResource);
	}

	@GetMapping("/isResourceAvailable/{idResource}")
	@Operation(summary = "isResourceAvailable", description = "Verifier la disponibilite d'une ressource")
	public boolean isResourceAvailable(
			@Parameter(name = "idResource", description = "idResource", required = true)
			@PathVariable("idResource") Long idResource) {
		return resourceService.isResourceAvailable(idResource);
	}
}
