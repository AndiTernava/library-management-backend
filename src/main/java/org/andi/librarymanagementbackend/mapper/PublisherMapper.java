package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.PublisherDto;
import org.andi.librarymanagementbackend.model.Publisher;

public class PublisherMapper {
    public static PublisherDto toDto(Publisher p) {
        return new PublisherDto(p.getId(), p.getName());
    }

    public static Publisher toEntity(PublisherDto dto) {
        Publisher p = new Publisher();
        p.setId(dto.getId());
        p.setName(dto.getName());
        return p;
    }
}
