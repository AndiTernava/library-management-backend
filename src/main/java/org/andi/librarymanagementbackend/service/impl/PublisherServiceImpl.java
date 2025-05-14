package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.PublisherDto;
import org.andi.librarymanagementbackend.mapper.PublisherMapper;
import org.andi.librarymanagementbackend.model.Publisher;
import org.andi.librarymanagementbackend.repository.PublisherRepository;
import org.andi.librarymanagementbackend.service.PublisherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository repo;

    public PublisherServiceImpl(PublisherRepository repo) {
        this.repo = repo;
    }

    @Override
    public PublisherDto createPublisher(PublisherDto dto) {
        Publisher p = PublisherMapper.toEntity(dto);
        return PublisherMapper.toDto(repo.save(p));
    }

    @Override
    public PublisherDto getPublisherById(Long id) {
        Publisher p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found: " + id));
        return PublisherMapper.toDto(p);
    }

    @Override
    public List<PublisherDto> getAllPublishers() {
        return repo.findAll()
                .stream()
                .map(PublisherMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PublisherDto updatePublisher(Long id, PublisherDto dto) {
        Publisher p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found: " + id));
        p.setName(dto.getName());
        return PublisherMapper.toDto(repo.save(p));
    }

    @Override
    public void deletePublisher(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Publisher not found: " + id);
        }
        repo.deleteById(id);
    }
}

