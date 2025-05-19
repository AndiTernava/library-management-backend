// src/main/java/org/andi/librarymanagementbackend/controller/LoanController.java
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

    /**
     * Get active loans for the current user
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<LoanDto>> activeLoans(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.getActiveLoans(tenantId));
    }

    /**
     * Get loan history for the current user
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/history")
    public ResponseEntity<List<LoanDto>> loanHistory(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(loanService.getLoanHistory(tenantId));
    }

    /**
     * Return a book
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/return")
    public ResponseEntity<Void> returnLoan(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        loanService.returnLoan(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}

