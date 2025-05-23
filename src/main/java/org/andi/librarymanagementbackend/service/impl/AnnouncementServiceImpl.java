// src/main/java/org/andi/librarymanagementbackend/service/impl/AnnouncementServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.AnnouncementDto;
import org.andi.librarymanagementbackend.mapper.AnnouncementMapper;
import org.andi.librarymanagementbackend.model.Announcement;
import org.andi.librarymanagementbackend.repository.AnnouncementRepository;
import org.andi.librarymanagementbackend.service.AnnouncementService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing announcements.
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repo;

    /**
     * Constructor.
     *
     * @param repo the AnnouncementRepository
     */
    public AnnouncementServiceImpl(AnnouncementRepository repo) {
        this.repo = repo;
    }

    /**
     * Create a new announcement.
     *
     * @param dto the announcement DTO
     * @return the saved announcement DTO
     */
    @Override
    public AnnouncementDto createAnnouncement(AnnouncementDto dto) {
        Announcement toSave = AnnouncementMapper.toEntity(dto);
        toSave.setId(null);
        Announcement saved = repo.save(toSave);
        return AnnouncementMapper.toDto(saved);
    }

    /**
     * Get an announcement by its ID.
     *
     * @param id the announcement ID
     * @return the announcement DTO
     * @throws ResponseStatusException if not found
     */
    @Override
    public AnnouncementDto getAnnouncementById(Long id) {
        Announcement a = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
        return AnnouncementMapper.toDto(a);
    }

    /**
     * Get all announcements.
     *
     * @return list of announcement DTOs
     */
    @Override
    public List<AnnouncementDto> getAllAnnouncements() {
        return repo.findAll()
                .stream()
                .map(AnnouncementMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing announcement.
     *
     * @param id  the announcement ID
     * @param dto the updated announcement DTO
     * @return the updated announcement DTO
     * @throws ResponseStatusException if not found
     */
    @Override
    public AnnouncementDto updateAnnouncement(Long id, AnnouncementDto dto) {
        Announcement existing = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setPublishDate(dto.getPublishDate());
        Announcement updated = repo.save(existing);
        return AnnouncementMapper.toDto(updated);
    }

    /**
     * Delete an announcement by ID.
     *
     * @param id the announcement ID
     * @throws ResponseStatusException if not found
     */
    @Override
    public void deleteAnnouncement(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        repo.deleteById(id);
    }
}
