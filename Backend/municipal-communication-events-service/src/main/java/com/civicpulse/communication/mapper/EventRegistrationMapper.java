package com.civicpulse.communication.mapper;

import com.civicpulse.communication.domain.entity.EventRegistration;
import com.civicpulse.communication.dto.response.EventRegistrationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventRegistrationMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    EventRegistrationResponse toResponse(EventRegistration registration);
}

