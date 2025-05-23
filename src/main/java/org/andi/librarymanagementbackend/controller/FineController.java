// src/main/java/org/andi/librarymanagementbackend/controller/FineController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.FineDto;
import org.andi.librarymanagementbackend.service.FineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing fines.
 */
@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    /**
     * List all unpaid fines for a specific user.
     *
     * @param userId the ID of the user
     * @return list of FineDto (unpaid only)
     */
    @GetMapping("/user/{userId}")
    public List<FineDto> listUnpaid(@PathVariable Long userId) {
        return fineService.getUnpaidFines(userId);
    }

    /**
     * Issue a $10 fine to a user immediately.
     *
     * @param userId the user to fine
     * @return the newly created FineDto
     */
    @PostMapping("/user/{userId}/apply")
    public FineDto applyFine(@PathVariable Long userId) {
        return fineService.applyFine(userId);
    }

    /**
     * Mark an existing fine as paid.
     *
     * @param fineId the ID of the fine to mark as paid
     * @return 200 OK on success
     */
    @PostMapping("/{fineId}/pay")
    public ResponseEntity<Void> pay(@PathVariable Long fineId) {
        fineService.markAsPaid(fineId);
        return ResponseEntity.ok().build();
    }
}

