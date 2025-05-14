package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.CategoryDto;
import org.andi.librarymanagementbackend.mapper.CategoryMapper;
import org.andi.librarymanagementbackend.model.Category;
import org.andi.librarymanagementbackend.repository.CategoryRepository;
import org.andi.librarymanagementbackend.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository repo, CategoryMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<CategoryDto> getAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long id) {
        Category cat = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapper.toDto(cat);
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category entity = mapper.toEntity(dto);
        Category saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        Category updated = repo.save(existing);
        return mapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
