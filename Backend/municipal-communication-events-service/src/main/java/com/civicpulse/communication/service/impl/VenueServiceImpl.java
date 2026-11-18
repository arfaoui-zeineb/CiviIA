package com.civicpulse.communication.service.impl;

import com.civicpulse.communication.domain.entity.Venue;
import com.civicpulse.communication.dto.request.VenueRequest;
import com.civicpulse.communication.dto.response.VenueResponse;
import com.civicpulse.communication.exception.ResourceNotFoundException;
import com.civicpulse.communication.mapper.VenueMapper;
import com.civicpulse.communication.repository.VenueRepository;
import com.civicpulse.communication.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);
        Venue saved = venueRepository.save(venue);
        return venueMapper.toResponse(saved);
    }

    @Override
    public VenueResponse getVenueById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        return venueMapper.toResponse(venue);
    }

    @Override
    public Page<VenueResponse> getAllVenues(Pageable pageable) {
        return venueRepository.findAll(pageable)
                .map(venueMapper::toResponse);
    }

    @Override
    @Transactional
    public VenueResponse updateVenue(Long id, VenueRequest request) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        venueMapper.updateEntityFromRequest(request, venue);
        return venueMapper.toResponse(venue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venue", id);
        }
        venueRepository.deleteById(id);
    }
}

