// src/main/java/org/andi/librarymanagementbackend/controller/AnnouncementController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AnnouncementDto;
import org.andi.librarymanagementbackend.service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing announcements.
 */
@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    /**
     * Retrieve all announcements.
     *
     * @param tenantId X-Tenant-ID header for multi-tenant support
     * @return 200 OK with list of AnnouncementDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AnnouncementDto>> getAll(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(service.getAllAnnouncements());
    }

    /**
     * Retrieve a single announcement by ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the ID of the announcement to retrieve
     * @return 200 OK with the AnnouncementDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getAnnouncementById(id));
    }

    /**
     * Create a new announcement.
     *
     * @param tenantId X-Tenant-ID header
     * @param dto      the announcement data to create
     * @return 201 Created with the created AnnouncementDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<AnnouncementDto> create(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody AnnouncementDto dto) {
        AnnouncementDto created = service.createAnnouncement(dto);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Update an existing announcement.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the ID of the announcement to update
     * @param dto      the new announcement data
     * @return 200 OK with the updated AnnouncementDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDto> update(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody AnnouncementDto dto) {
        return ResponseEntity.ok(service.updateAnnouncement(id, dto));
    }

    /**
     * Delete an announcement by ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the ID of the announcement to delete
     * @return 204 No Content on successful deletion
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        service.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}
