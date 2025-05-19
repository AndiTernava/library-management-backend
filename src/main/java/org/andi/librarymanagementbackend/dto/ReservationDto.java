package org.andi.librarymanagementbackend.dto;

import java.time.LocalDate;

public record ReservationDto(
        Long id,
        Long bookId,
        Long userId,
        LocalDate loanDate,
        LocalDate dueDate,
        boolean returned,
        String status
) {}
