package com.civicpulse.communication.mapper;

import com.civicpulse.communication.domain.entity.Announcement;
import com.civicpulse.communication.dto.request.AnnouncementRequest;
import com.civicpulse.communication.dto.response.AnnouncementResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AttachmentMapper.class})
public interface AnnouncementMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "eventId", source = "event.id")
    AnnouncementResponse toResponse(Announcement announcement);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Announcement toEntity(AnnouncementRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(AnnouncementRequest request, @MappingTarget Announcement announcement);
}

