package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.AnnouncementDto;
import org.andi.librarymanagementbackend.model.Announcement;

public class AnnouncementMapper {

    public static AnnouncementDto toDto(Announcement a) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setPublishDate(a.getPublishDate());
        return dto;
    }

    public static Announcement toEntity(AnnouncementDto dto) {
        Announcement a = new Announcement();
        a.setId(dto.getId());
        a.setTitle(dto.getTitle());
        a.setContent(dto.getContent());
        a.setPublishDate(dto.getPublishDate());
        return a;
    }
}
