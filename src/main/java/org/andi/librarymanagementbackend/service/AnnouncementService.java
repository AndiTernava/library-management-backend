package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.AnnouncementDto;

import java.util.List;

public interface AnnouncementService {
    AnnouncementDto createAnnouncement(AnnouncementDto dto);
    AnnouncementDto getAnnouncementById(Long id);
    List<AnnouncementDto> getAllAnnouncements();
    AnnouncementDto updateAnnouncement(Long id, AnnouncementDto dto);
    void deleteAnnouncement(Long id);
}
