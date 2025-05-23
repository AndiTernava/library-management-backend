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

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper     mapper;

    public CategoryServiceImpl(CategoryRepository repo, CategoryMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }

    @Override
    @Cacheable(value = "categories")
    public List<CategoryDto> getAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public CategoryDto getById(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapper.toDto(c);
    }

    @Override
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public CategoryDto create(CategoryDto dto) {
        Category e = mapper.toEntity(dto);
        return mapper.toDto(repo.save(e));
    }

    @Override
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public CategoryDto update(Long id, CategoryDto dto) {
        Category e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        return mapper.toDto(repo.save(e));
    }

    @Override
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
