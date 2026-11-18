package com.civicpulse.communication.service;

import com.civicpulse.communication.dto.request.VenueRequest;
import com.civicpulse.communication.dto.response.VenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VenueService {
    VenueResponse createVenue(VenueRequest request);
    VenueResponse getVenueById(Long id);
    Page<VenueResponse> getAllVenues(Pageable pageable);
    VenueResponse updateVenue(Long id, VenueRequest request);
    void deleteVenue(Long id);
}

