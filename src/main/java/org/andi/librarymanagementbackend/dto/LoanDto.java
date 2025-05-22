package org.andi.librarymanagementbackend.dto;

import java.time.LocalDate;

public record LoanDto(
        Long id,
        Long bookId,
        String bookTitle,
        String authorName,
        Long userId,
        String userFullName,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        boolean returned,
        String status,
        String returnStatus
) {}