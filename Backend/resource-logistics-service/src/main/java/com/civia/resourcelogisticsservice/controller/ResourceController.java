package com.civia.resourcelogisticsservice.controller;

import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.service.IResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/resources", "/Resource"})
public class ResourceController {

    private final IResourceService resourceService;

    @PostMapping({"", "/addResource"})
    public Resource addResource(@RequestBody Resource resource) {
        return resourceService.addResource(resource);
    }

    @GetMapping({"", "/getResources"})
    public List<Resource> getAllResources() {
        return resourceService.retrieveAllResources();
    }

    @GetMapping({"/{id}", "/getResourceId/{id}"})
    public Resource getResourceById(@PathVariable Long id) {
        return resourceService.retrieveResource(id);
    }

    @PutMapping("/{id}")
    public Resource updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        resource.setId(id);
        return resourceService.updateResource(resource);
    }

    @PutMapping("/updateResource")
    public Resource updateResource(@RequestBody Resource resource) {
        return resourceService.updateResource(resource);
    }

    @DeleteMapping({"/{id}", "/deleteResource/{id}"})
    public void deleteResource(@PathVariable Long id) {
        resourceService.removeResource(id);
    }

    @GetMapping({"/{id}/availability", "/isResourceAvailable/{id}"})
    public boolean isResourceAvailable(@PathVariable Long id,
                                       @RequestParam(required = false) LocalDateTime startDateTime,
                                       @RequestParam(required = false) LocalDateTime endDateTime) {
        if (startDateTime != null && endDateTime != null) {
            return resourceService.isResourceAvailable(id, startDateTime, endDateTime);
        }
        return resourceService.isResourceAvailable(id);
    }

    @PutMapping({"/{id}/status", "/changeResourceStatus/{id}"})
    public Resource changeStatus(@PathVariable Long id, @RequestParam ResourceStatus status) {
        return resourceService.changeResourceStatus(id, status);
    }
}
