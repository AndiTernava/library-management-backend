package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.ReviewDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.Review;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.ReviewRepository;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public ReviewServiceImpl(ReviewRepository reviewRepo,
                             BookRepository bookRepo,
                             UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ReviewDto createReview(Long bookId, String comment, int rating, Long userId, String tenantId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        User user = userRepo.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Review r = new Review();
        r.setBook(book);
        r.setUser(user);
        r.setComment(comment);
        r.setRating(rating);
        r.setTenantId(tenantId);

        Review saved = reviewRepo.save(r);
        return toDto(saved);
    }

    @Override
    public List<ReviewDto> findMine(String tenantId, Long userId) {
        return reviewRepo.findByUserIdAndTenantId(userId, tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findAll(String tenantId) {
        return reviewRepo.findByTenantId(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDto> findByBook(Long bookId, String tenantId) {
        return reviewRepo.findByBookIdAndTenantId(bookId, tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReviewDto toDto(Review rv) {
        return new ReviewDto(
                rv.getId(),
                rv.getBook().getId(),
                rv.getUser().getId(),
                rv.getUser().getFullName(),
                rv.getComment(),
                rv.getRating()
        );
    }
}