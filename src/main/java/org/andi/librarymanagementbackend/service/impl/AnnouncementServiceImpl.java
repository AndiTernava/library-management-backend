package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.AnnouncementDto;
import org.andi.librarymanagementbackend.mapper.AnnouncementMapper;
import org.andi.librarymanagementbackend.model.Announcement;
import org.andi.librarymanagementbackend.repository.AnnouncementRepository;
import org.andi.librarymanagementbackend.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repo;

    public AnnouncementServiceImpl(AnnouncementRepository repo) {
        this.repo = repo;
    }

    @Override
    public AnnouncementDto createAnnouncement(AnnouncementDto dto) {
        // Map DTO → Entity, then clear any ID so JPA does an INSERT
        Announcement toSave = AnnouncementMapper.toEntity(dto);
        toSave.setId(null);
        Announcement saved = repo.save(toSave);
        return AnnouncementMapper.toDto(saved);
    }

    @Override
    public AnnouncementDto getAnnouncementById(Long id) {
        Announcement a = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
        return AnnouncementMapper.toDto(a);
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncements() {
        return repo.findAll()
                .stream()
                .map(AnnouncementMapper::toDto)
                .collect(Collectors.toList());
    }

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

    @Override
    public void deleteAnnouncement(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        repo.deleteById(id);
    }
}
