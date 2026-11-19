package com.civia.resourcelogisticsservice.service;

import com.civia.resourcelogisticsservice.domain.ReservationStatus;
import com.civia.resourcelogisticsservice.domain.Resource;
import com.civia.resourcelogisticsservice.domain.ResourceStatus;
import com.civia.resourcelogisticsservice.repository.ResourceRepository;
import com.civia.resourcelogisticsservice.repository.ResourceReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ResourceService implements IResourceService {

    final ResourceRepository resourceRepo;
    final ResourceReservationRepository reservationRepo;

    @Override
    public List<Resource> retrieveAllResources() {
        return (List<Resource>) resourceRepo.findAll();
    }

    @Override
    public Resource addResource(Resource resource) {
        return resourceRepo.save(resource);
    }

    @Override
    public Resource updateResource(Resource resource) {
        return resourceRepo.save(resource);
    }

    @Override
    public Resource retrieveResource(Long idResource) {
        return resourceRepo.findById(idResource)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + idResource));
    }

    @Override
    public void removeResource(Long idResource) {
        resourceRepo.deleteById(idResource);
    }

    @Override
    public boolean isResourceAvailable(Long idResource) {
        return retrieveResource(idResource).isAvailable();
    }

    @Override
    public boolean isResourceAvailable(Long idResource, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Resource resource = retrieveResource(idResource);
        if (!resource.isAvailable()) {
            return false;
        }
        return reservationRepo.findByResourceIdAndStatusIn(idResource, ReservationStatus.ACTIVE)
                .stream()
                .noneMatch(r -> r.overlaps(startDateTime, endDateTime));
    }

    @Override
    public Resource changeResourceStatus(Long idResource, ResourceStatus status) {
        Resource resource = retrieveResource(idResource);
        resource.changeStatus(status);
        return resourceRepo.save(resource);
    }
}
