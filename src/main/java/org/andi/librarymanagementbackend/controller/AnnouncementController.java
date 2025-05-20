package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AnnouncementDto;
import org.andi.librarymanagementbackend.service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    // GET all announcements
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AnnouncementDto>> getAll(
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        return ResponseEntity.ok(service.getAllAnnouncements());
    }

    // GET by id
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getAnnouncementById(id));
    }

    // POST create
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<AnnouncementDto> create(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody AnnouncementDto dto
    ) {
        AnnouncementDto created = service.createAnnouncement(dto);
        return ResponseEntity.status(201).body(created);
    }

    // PUT update
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDto> update(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody AnnouncementDto dto
    ) {
        return ResponseEntity.ok(service.updateAnnouncement(id, dto));
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        service.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}
