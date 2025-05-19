package org.andi.librarymanagementbackend.dto;

import java.time.LocalDate;

public record LoanDto(
        Long id,
        Long bookId,
        Long userId,
        LocalDate loanDate,
        LocalDate dueDate,
        boolean returned
) {}
