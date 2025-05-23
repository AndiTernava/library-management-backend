// src/main/java/org/andi/librarymanagementbackend/controller/ReservationController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.ReservationDto;
import org.andi.librarymanagementbackend.dto.ReservationDetailsDto;
import org.andi.librarymanagementbackend.security.CustomUserDetails;
import org.andi.librarymanagementbackend.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing reservations.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService resService;

    public ReservationController(ReservationService resService) {
        this.resService = resService;
    }

    /**
     * List reservations.
     * - Librarians see all detailed reservations.
     * - Members see only their own basic reservations.
     *
     * @param user     the authenticated user principal
     * @param tenantId X-Tenant-ID header
     * @return list of ReservationDto or ReservationDetailsDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        boolean isLibrarian = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));

        if (isLibrarian) {
            List<ReservationDetailsDto> all = resService.getAll(tenantId);
            return ResponseEntity.ok(all);
        } else {
            List<ReservationDto> mine = resService.findByUser(user.getId(), tenantId);
            return ResponseEntity.ok(mine);
        }
    }

    /**
     * Create a reservation (member or librarian).
     *
     * @param dto      the reservation data
     * @param user     the authenticated user principal
     * @param tenantId X-Tenant-ID header
     * @return the created ReservationDto
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ReservationDto> create(
            @RequestBody ReservationDto dto,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        ReservationDto corrected = new ReservationDto(
                dto.id(),
                dto.bookId(),
                user.getId(),
                dto.loanDate(),
                dto.dueDate(),
                dto.returned(),
                dto.status()
        );
        return ResponseEntity.ok(resService.create(corrected, tenantId));
    }

    /**
     * Cancel a reservation.
     *
     * @param id       the reservation ID
     * @param tenantId X-Tenant-ID header
     * @return 204 No Content on success
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        resService.cancel(id, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update reservation status (librarian only).
     *
     * @param id        the reservation ID
     * @param status    the new status
     * @param tenantId  X-Tenant-ID header
     * @return updated ReservationDetailsDto
     */
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationDetailsDto> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        ReservationDetailsDto updated = resService.updateStatus(id, status, tenantId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Check book availability.
     *
     * @param bookId   the book ID to check
     * @param user     the authenticated user principal
     * @param tenantId X-Tenant-ID header
     * @return map with 'available': true/false
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check/{bookId}")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long bookId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        boolean available = resService.checkAvailability(bookId, user.getId(), tenantId);
        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * Confirm pickup of an approved reservation (librarian only).
     *
     * @param id       the reservation ID
     * @param tenantId X-Tenant-ID header
     * @return updated ReservationDetailsDto
     */
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PatchMapping("/{id}/pickup")
    public ResponseEntity<ReservationDetailsDto> confirmPickup(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        ReservationDetailsDto updated = resService.confirmPickup(id, tenantId);
        return ResponseEntity.ok(updated);
    }
}
