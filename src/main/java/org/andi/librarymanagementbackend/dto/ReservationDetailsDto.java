package org.andi.librarymanagementbackend.dto;

import java.time.LocalDate;

public record ReservationDetailsDto(
        Long id,
        Long bookId,
        String title,
        String authorName,
        Long userId,
        String userFullName,
        LocalDate loanDate,
        LocalDate dueDate,
        String status
) {}