package org.andi.librarymanagementbackend.dto;

public record ReviewDto(
        Long id,
        Long bookId,
        Long userId,
        String userFullName,
        String comment,
        int rating
) {}
