package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.ReviewDto;
import org.andi.librarymanagementbackend.security.CustomUserDetails;
import org.andi.librarymanagementbackend.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService svc;

    public ReviewController(ReviewService svc) {
        this.svc = svc;
    }

    /** Create a review (members only) */
    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ReviewDto> create(
            @RequestBody CreateReviewRequest req,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        ReviewDto dto = svc.createReview(
                req.bookId(), req.comment(), req.rating(),
                user.getId(), tenantId
        );
        return ResponseEntity.ok(dto);
    }

    /** List either all (librarian) or just own (member) */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewDto>> list(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        boolean isLib = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));

        List<ReviewDto> out = isLib
                ? svc.findAll(tenantId)
                : svc.findMine(tenantId, user.getId());

        return ResponseEntity.ok(out);
    }

    /** List reviews for a given book (open to all authenticated) */
    @GetMapping("/book/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewDto>> forBook(
            @PathVariable Long bookId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        return ResponseEntity.ok(svc.findByBook(bookId, tenantId));
    }

    public record CreateReviewRequest(
            Long bookId,
            String comment,
            int rating
    ) {}
}