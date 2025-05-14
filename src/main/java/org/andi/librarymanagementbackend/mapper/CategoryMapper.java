package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.CategoryDto;
import org.andi.librarymanagementbackend.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category entity) {
        if (entity == null) return null;
        return new CategoryDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;
        Category cat = new Category();
        cat.setId(dto.getId());
        cat.setName(dto.getName());
        cat.setDescription(dto.getDescription());
        return cat;
    }
}
