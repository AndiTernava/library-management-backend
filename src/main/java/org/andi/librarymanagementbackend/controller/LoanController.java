// src/main/java/org/andi/librarymanagementbackend/controller/LoanController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.LoanDto;
import org.andi.librarymanagementbackend.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing book loans.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Get all currently active (not returned) loans for the tenant.
     *
     * @param tenantId X-Tenant-ID header
     * @return list of LoanDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<LoanDto>> activeLoans(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.getActiveLoans(tenantId));
    }

    /**
     * Get full loan history (all returned and active) for the tenant.
     *
     * @param tenantId X-Tenant-ID header
     * @return list of LoanDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/history")
    public ResponseEntity<List<LoanDto>> loanHistory(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.getLoanHistory(tenantId));
    }

    /**
     * Return a loan by ID. Issues fine if late.
     *
     * @param id       the loan ID
     * @param tenantId X-Tenant-ID header
     * @return updated LoanDto
     */
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnLoan(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.returnLoan(id, tenantId));
    }

    /**
     * Create a loan from an existing reservation.
     *
     * @param reservationId the reservation ID
     * @param tenantId      X-Tenant-ID header
     * @return the new LoanDto
     */
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping("/from-reservation/{reservationId}")
    public ResponseEntity<LoanDto> createLoanFromReservation(
            @PathVariable Long reservationId,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(
                loanService.createLoanFromReservation(reservationId, tenantId)
        );
    }
}
