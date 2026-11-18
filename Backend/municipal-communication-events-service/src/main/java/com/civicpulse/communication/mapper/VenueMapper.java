package com.civicpulse.communication.mapper;

import com.civicpulse.communication.domain.entity.Venue;
import com.civicpulse.communication.dto.request.VenueRequest;
import com.civicpulse.communication.dto.response.VenueResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    VenueResponse toResponse(Venue venue);
    Venue toEntity(VenueRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(VenueRequest request, @MappingTarget Venue venue);
}

