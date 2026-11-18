package com.civicpulse.communication.mapper;

import com.civicpulse.communication.domain.entity.Category;
import com.civicpulse.communication.dto.request.CategoryRequest;
import com.civicpulse.communication.dto.response.CategoryResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
    Category toEntity(CategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}

