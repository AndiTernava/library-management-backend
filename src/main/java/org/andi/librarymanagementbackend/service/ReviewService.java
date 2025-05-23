package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto createReview(Long bookId, String comment, int rating, Long userId, String tenantId);
    List<ReviewDto> findMine(String tenantId, Long userId);
    List<ReviewDto> findAll(String tenantId);
    List<ReviewDto> findByBook(Long bookId, String tenantId);
}