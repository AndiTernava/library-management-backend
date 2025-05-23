package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.LibraryCardDto;
import org.andi.librarymanagementbackend.service.LibraryCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library-cards")
@CrossOrigin(origins = "*")
public class LibraryCardController {

    private final LibraryCardService service;

    public LibraryCardController(LibraryCardService service) {
        this.service = service;
    }

    // Only allow the MEMBER role to get their own card
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/me")
    public ResponseEntity<LibraryCardDto> getMyCard(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }
}