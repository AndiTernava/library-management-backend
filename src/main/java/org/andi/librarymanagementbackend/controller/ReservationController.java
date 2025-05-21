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

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService resService;

    public ReservationController(ReservationService resService) {
        this.resService = resService;
    }

    /**
     * Members get only their own reservations.
     * Librarians get all reservations with full details.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        boolean isLibrarian = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));

        if (isLibrarian) {
            // Return detailed view for librarians
            List<ReservationDetailsDto> all = resService.getAll(tenantId);
            return ResponseEntity.ok(all);
        } else {
            // Members get only their own basic reservations
            List<ReservationDto> mine = resService.findByUser(user.getId(), tenantId);
            return ResponseEntity.ok(mine);
        }
    }

    /** Create a reservation (any authenticated user) */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationDto> create(
            @RequestBody ReservationDto dto,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        return ResponseEntity.ok(resService.create(dto, tenantId));
    }

    /** Cancel a reservation (any authenticated user) */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        resService.cancel(id, tenantId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ReservationDetailsDto> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        ReservationDetailsDto updated = resService.updateStatus(id, status, tenantId);
        return ResponseEntity.ok(updated);
    }






    /** Check availability (any authenticated user) */
    @GetMapping("/check/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long bookId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        boolean available = resService.checkAvailability(bookId, tenantId);
        return ResponseEntity.ok(Map.of("available", available));
    }
}

