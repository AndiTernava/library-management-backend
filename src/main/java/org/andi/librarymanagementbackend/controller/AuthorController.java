package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AuthorDto;
import org.andi.librarymanagementbackend.service.AuthorService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors(
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody AuthorDto authorDto
    ) {
        return ResponseEntity.ok(authorService.createAuthor(authorDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody AuthorDto authorDto
    ) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

