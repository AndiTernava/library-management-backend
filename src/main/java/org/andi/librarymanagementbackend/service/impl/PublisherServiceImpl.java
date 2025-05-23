// src/main/java/org/andi/librarymanagementbackend/service/impl/PublisherServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.PublisherDto;
import org.andi.librarymanagementbackend.mapper.PublisherMapper;
import org.andi.librarymanagementbackend.model.Publisher;
import org.andi.librarymanagementbackend.repository.PublisherRepository;
import org.andi.librarymanagementbackend.service.PublisherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing publishers.
 */
@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository repo;

    /**
     * Constructor.
     *
     * @param repo the PublisherRepository
     */
    public PublisherServiceImpl(PublisherRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new publisher.
     *
     * @param dto the publisher DTO
     * @return the created publisher DTO
     */
    @Override
    public PublisherDto createPublisher(PublisherDto dto) {
        Publisher p = PublisherMapper.toEntity(dto);
        return PublisherMapper.toDto(repo.save(p));
    }

    /**
     * Get a publisher by ID.
     *
     * @param id the publisher ID
     * @return the publisher DTO
     */
    @Override
    public PublisherDto getPublisherById(Long id) {
        Publisher p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found: " + id));
        return PublisherMapper.toDto(p);
    }

    /**
     * Get all publishers.
     *
     * @return list of publisher DTOs
     */
    @Override
    public List<PublisherDto> getAllPublishers() {
        return repo.findAll()
                .stream()
                .map(PublisherMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing publisher.
     *
     * @param id  the publisher ID
     * @param dto the updated publisher DTO
     * @return the updated publisher DTO
     */
    @Override
    public PublisherDto updatePublisher(Long id, PublisherDto dto) {
        Publisher p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found: " + id));
        p.setName(dto.getName());
        return PublisherMapper.toDto(repo.save(p));
    }

    /**
     * Delete a publisher by ID.
     *
     * @param id the publisher ID
     */
    @Override
    public void deletePublisher(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Publisher not found: " + id);
        }
        repo.deleteById(id);
    }
}
