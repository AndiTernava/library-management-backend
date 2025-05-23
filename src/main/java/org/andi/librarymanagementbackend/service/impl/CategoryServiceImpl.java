// src/main/java/org/andi/librarymanagementbackend/service/impl/CategoryServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.CategoryDto;
import org.andi.librarymanagementbackend.mapper.CategoryMapper;
import org.andi.librarymanagementbackend.model.Category;
import org.andi.librarymanagementbackend.repository.CategoryRepository;
import org.andi.librarymanagementbackend.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing categories.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper     mapper;

    /**
     * Constructor.
     *
     * @param repo   the CategoryRepository
     * @param mapper the CategoryMapper
     */
    public CategoryServiceImpl(CategoryRepository repo, CategoryMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }

    /**
     * Get all categories.
     *
     * @return list of category DTOs
     */
    @Override
    @Cacheable(value = "categories")
    public List<CategoryDto> getAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a category by ID.
     *
     * @param id the category ID
     * @return the category DTO
     */
    @Override
    @Cacheable(value = "category", key = "#id")
    public CategoryDto getById(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapper.toDto(c);
    }

    /**
     * Create a new category.
     *
     * @param dto the category DTO
     * @return the created category DTO
     */
    @Override
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public CategoryDto create(CategoryDto dto) {
        Category e = mapper.toEntity(dto);
        return mapper.toDto(repo.save(e));
    }

    /**
     * Update an existing category.
     *
     * @param id  the category ID
     * @param dto the updated category DTO
     * @return the updated category DTO
     */
    @Override
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public CategoryDto update(Long id, CategoryDto dto) {
        Category e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        return mapper.toDto(repo.save(e));
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID
     */
    @Override
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
