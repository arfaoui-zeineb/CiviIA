package com.civicpulse.communication.controller;

import com.civicpulse.communication.dto.request.VenueRequest;
import com.civicpulse.communication.dto.response.VenueResponse;
import com.civicpulse.communication.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
@Tag(name = "Venues", description = "Endpoints for managing municipal venues")
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @Operation(summary = "Create a venue")
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        VenueResponse response = venueService.createVenue(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a venue by ID")
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }

    @GetMapping
    @Operation(summary = "Get all venues (paginated)")
    public ResponseEntity<Page<VenueResponse>> getAllVenues(Pageable pageable) {
        return ResponseEntity.ok(venueService.getAllVenues(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a venue (full replacement)")
    public ResponseEntity<VenueResponse> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        return ResponseEntity.ok(venueService.updateVenue(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a venue")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}

