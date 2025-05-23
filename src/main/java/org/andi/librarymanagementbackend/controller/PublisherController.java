// src/main/java/org/andi/librarymanagementbackend/controller/PublisherController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.PublisherDto;
import org.andi.librarymanagementbackend.service.PublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing publishers.
 */
@RestController
@RequestMapping("/api/publishers")
@CrossOrigin(origins = "*")
public class PublisherController {

    private final PublisherService svc;

    public PublisherController(PublisherService svc) {
        this.svc = svc;
    }

    /**
     * Get all publishers.
     *
     * @param tenantId X-Tenant-ID header
     * @return list of PublisherDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PublisherDto>> getAllPublishers(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(svc.getAllPublishers());
    }

    /**
     * Get a publisher by ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the publisher ID
     * @return the PublisherDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PublisherDto> getPublisherById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        return ResponseEntity.ok(svc.getPublisherById(id));
    }

    /**
     * Create a new publisher.
     *
     * @param tenantId X-Tenant-ID header
     * @param dto      the publisher data
     * @return 200 OK with created PublisherDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<PublisherDto> createPublisher(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody PublisherDto dto) {
        return ResponseEntity.ok(svc.createPublisher(dto));
    }

    /**
     * Update an existing publisher.
     *
     * @param tenantId X-Tenant-ID header
     * @param id        the publisher ID
     * @param dto       the updated data
     * @return 200 OK with updated PublisherDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PublisherDto> updatePublisher(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody PublisherDto dto) {
        return ResponseEntity.ok(svc.updatePublisher(id, dto));
    }

    /**
     * Delete a publisher.
     *
     * @param tenantId X-Tenant-ID header
     * @param id        the publisher ID
     * @return 204 No Content on success
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        svc.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
