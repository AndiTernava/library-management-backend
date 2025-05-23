// src/main/java/org/andi/librarymanagementbackend/service/impl/AuthorServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.AuthorDto;
import org.andi.librarymanagementbackend.mapper.AuthorMapper;
import org.andi.librarymanagementbackend.model.Author;
import org.andi.librarymanagementbackend.repository.AuthorRepository;
import org.andi.librarymanagementbackend.service.AuthorService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing authors.
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Constructor.
     *
     * @param authorRepository the AuthorRepository
     */
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Create a new author.
     *
     * @param dto the author DTO
     * @return the saved author DTO
     */
    @Override
    @CacheEvict(value = "authors", allEntries = true)
    public AuthorDto createAuthor(AuthorDto dto) {
        Author a = AuthorMapper.toEntity(dto);
        return AuthorMapper.toDto(authorRepository.save(a));
    }

    /**
     * Get an author by ID.
     *
     * @param id the author ID
     * @return the author DTO
     */
    @Override
    @Cacheable(value = "author", key = "#id")
    public AuthorDto getAuthorById(Long id) {
        Author a = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        return AuthorMapper.toDto(a);
    }

    /**
     * Get all authors.
     *
     * @return list of author DTOs
     */
    @Override
    @Cacheable(value = "authors")
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing author.
     *
     * @param id  the author ID
     * @param dto the updated author DTO
     * @return the updated author DTO
     */
    @Override
    @CacheEvict(value = { "authors", "author" }, allEntries = true)
    public AuthorDto updateAuthor(Long id, AuthorDto dto) {
        Author a = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        a.setName(dto.getName());
        return AuthorMapper.toDto(authorRepository.save(a));
    }

    /**
     * Delete an author by ID.
     *
     * @param id the author ID
     */
    @Override
    @CacheEvict(value = { "authors", "author" }, allEntries = true)
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}
