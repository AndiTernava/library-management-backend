package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AuthorDto;
import org.andi.librarymanagementbackend.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors(
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody AuthorDto authorDto
    ) {
        return ResponseEntity.ok(authorService.createAuthor(authorDto));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody AuthorDto authorDto
    ) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

