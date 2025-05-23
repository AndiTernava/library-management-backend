package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserIdAndTenantId(Long userId, String tenantId);
    List<Review> findByTenantId(String tenantId);
    List<Review> findByBookIdAndTenantId(Long bookId, String tenantId);
}