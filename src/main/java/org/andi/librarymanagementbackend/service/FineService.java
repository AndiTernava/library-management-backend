// src/main/java/org/andi/librarymanagementbackend/service/FineService.java
package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.FineDto;

import java.util.List;

public interface FineService {
    /** issues a $10 fine for the given user */
    FineDto applyFine(Long userId);

    /** return all unpaid fines for a user */
    List<FineDto> getUnpaidFines(Long userId);

    /** mark a fine as paid */
    void markAsPaid(Long fineId);
}
