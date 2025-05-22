package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.LoanDto;
import org.andi.librarymanagementbackend.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<LoanDto>> activeLoans(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.getActiveLoans(tenantId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/history")
    public ResponseEntity<List<LoanDto>> loanHistory(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.getLoanHistory(tenantId));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnLoan(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.returnLoan(id, tenantId));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping("/from-reservation/{reservationId}")
    public ResponseEntity<LoanDto> createLoanFromReservation(
            @PathVariable Long reservationId,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.createLoanFromReservation(reservationId, tenantId));
    }
}