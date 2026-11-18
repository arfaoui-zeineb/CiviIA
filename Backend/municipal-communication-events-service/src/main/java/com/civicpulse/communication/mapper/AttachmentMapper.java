package com.civicpulse.communication.mapper;

import com.civicpulse.communication.domain.entity.Attachment;
import com.civicpulse.communication.dto.response.AttachmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(target = "announcementId", source = "announcement.id")
    AttachmentResponse toResponse(Attachment attachment);
}

