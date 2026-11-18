package com.civicpulse.communication.mapper;

import com.civicpulse.communication.domain.entity.Event;
import com.civicpulse.communication.dto.request.EventRequest;
import com.civicpulse.communication.dto.response.EventResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "venueId", source = "venue.id")
    @Mapping(target = "venueName", source = "venue.name")
    EventResponse toResponse(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "registeredCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "registeredCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(EventRequest request, @MappingTarget Event event);
}

