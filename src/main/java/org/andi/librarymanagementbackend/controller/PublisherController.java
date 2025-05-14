package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.PublisherDto;
import org.andi.librarymanagementbackend.service.PublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@CrossOrigin(origins = "*")
public class PublisherController {

    private final PublisherService svc;

    public PublisherController(PublisherService svc) {
        this.svc = svc;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<PublisherDto>> getAllPublishers(
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        return ResponseEntity.ok(svc.getAllPublishers());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<PublisherDto> getPublisherById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(svc.getPublisherById(id));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<PublisherDto> createPublisher(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody PublisherDto dto
    ) {
        return ResponseEntity.ok(svc.createPublisher(dto));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PublisherDto> updatePublisher(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody PublisherDto dto
    ) {
        return ResponseEntity.ok(svc.updatePublisher(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        svc.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
