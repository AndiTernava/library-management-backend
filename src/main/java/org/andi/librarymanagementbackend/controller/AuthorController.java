// src/main/java/org/andi/librarymanagementbackend/controller/AuthorController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AuthorDto;
import org.andi.librarymanagementbackend.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing authors.
 */
@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Get all authors.
     *
     * @param tenantId X-Tenant-ID header
     * @return 200 OK with list of AuthorDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    /**
     * Get a single author by ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the ID of the author
     * @return 200 OK with the AuthorDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    /**
     * Create a new author.
     *
     * @param tenantId X-Tenant-ID header
     * @param authorDto data of the new author
     * @return 200 OK with the created AuthorDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.createAuthor(authorDto));
    }

    /**
     * Update an existing author.
     *
     * @param tenantId X-Tenant-ID header
     * @param id        the ID of the author to update
     * @param authorDto the updated data
     * @return 200 OK with the updated AuthorDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDto));
    }

    /**
     * Delete an author by ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the ID of the author to delete
     * @return 204 No Content on success
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
