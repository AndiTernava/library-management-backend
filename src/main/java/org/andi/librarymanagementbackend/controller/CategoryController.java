package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.CategoryDto;
import org.andi.librarymanagementbackend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing categories.
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    /**
     * Retrieve all categories.
     *
     * @param tenantId X-Tenant-ID header
     * @return 200 OK with list of CategoryDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Retrieve a category by ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the category ID
     * @return 200 OK with the CategoryDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Create a new category.
     *
     * @param tenantId X-Tenant-ID header
     * @param dto      the category data
     * @return 201 Created with the created CategoryDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDto> create(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody CategoryDto dto) {
        CategoryDto created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Update an existing category.
     *
     * @param tenantId X-Tenant-ID header
     * @param id        the ID of the category
     * @param dto       the updated data
     * @return 200 OK with the updated CategoryDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Delete a category by its ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id        the category ID
     * @return 204 No Content on success
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
