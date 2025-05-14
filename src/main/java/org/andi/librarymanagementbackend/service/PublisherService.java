package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.PublisherDto;
import java.util.List;

public interface PublisherService {
    PublisherDto createPublisher(PublisherDto dto);
    PublisherDto getPublisherById(Long id);
    List<PublisherDto> getAllPublishers();
    PublisherDto updatePublisher(Long id, PublisherDto dto);
    void deletePublisher(Long id);
}

