// src/main/java/org/andi/librarymanagementbackend/controller/UserController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users (admin only).
     *
     * @return list of UserDto
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get a user by ID (librarian or admin).
     *
     * @param id       the user ID
     * @param tenantId X-Tenant-ID header
     * @return the UserDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Create a new user (admin only).
     *
     * @param userDto  the user data
     * @param tenantId X-Tenant-ID header
     * @return the created UserDto
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    /**
     * Update an existing user (admin only).
     *
     * @param id        the user ID
     * @param userDto   the updated data
     * @param tenantId  X-Tenant-ID header
     * @return the updated UserDto
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    /**
     * Delete a user by ID (admin only).
     *
     * @param id       the user ID
     * @param tenantId X-Tenant-ID header
     * @return 204 No Content on success
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
