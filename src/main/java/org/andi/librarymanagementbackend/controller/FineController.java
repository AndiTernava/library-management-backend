// src/main/java/org/andi/librarymanagementbackend/controller/FineController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.FineDto;
import org.andi.librarymanagementbackend.service.FineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {
    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    /** list unpaid fines for a user */
    @GetMapping("/user/{userId}")
    public List<FineDto> listUnpaid(@PathVariable Long userId) {
        return fineService.getUnpaidFines(userId);
    }

    /** apply a $10 fine to a user */
    @PostMapping("/user/{userId}/apply")
    public FineDto applyFine(@PathVariable Long userId) {
        return fineService.applyFine(userId);
    }

    /** mark a fine as paid */
    @PostMapping("/{fineId}/pay")
    public ResponseEntity<Void> pay(@PathVariable Long fineId) {
        fineService.markAsPaid(fineId);
        return ResponseEntity.ok().build();
    }
}
