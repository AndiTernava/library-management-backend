package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.ReservationDto;
import org.andi.librarymanagementbackend.security.CustomUserDetails;
import org.andi.librarymanagementbackend.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService resService;
    public ReservationController(ReservationService resService) {
        this.resService = resService;
    }

    /**
     * Create a new reservation. Any authenticated user may reserve.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ReservationDto> create(
            @RequestBody ReservationDto dto,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(resService.create(dto, tenantId));
    }

    /**
     * List reservations for a given user. Any authenticated user may view their own reservations.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ReservationDto>> listByUser(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        return ResponseEntity.ok(resService.findByUser(userId, tenantId));
    }

    /**
     * Cancel a reservation. Any authenticated user may cancel their own reservation.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        resService.cancel(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}

