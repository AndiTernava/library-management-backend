// src/main/java/org/andi/librarymanagementbackend/service/impl/ReservationServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.ReservationDetailsDto;
import org.andi.librarymanagementbackend.dto.ReservationDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.Reservation;
import org.andi.librarymanagementbackend.model.Reservation.ReservationStatus;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.ReservationRepository;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.LoanService;
import org.andi.librarymanagementbackend.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing reservations.
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository resRepo;
    private final BookRepository        bookRepo;
    private final UserRepository        userRepo;
    private final LoanService           loanService;

    /**
     * Constructor.
     *
     * @param resRepo     the ReservationRepository
     * @param bookRepo    the BookRepository
     * @param userRepo    the UserRepository
     * @param loanService the LoanService
     */
    public ReservationServiceImpl(ReservationRepository resRepo,
                                  BookRepository bookRepo,
                                  UserRepository userRepo,
                                  LoanService loanService) {
        this.resRepo     = resRepo;
        this.bookRepo    = bookRepo;
        this.userRepo    = userRepo;
        this.loanService = loanService;
    }

    /**
     * Create a new reservation.
     *
     * @param dto      the reservation DTO
     * @param tenantId the tenant ID
     * @return the saved reservation DTO
     * @throws ResponseStatusException if book or user not found or status invalid
     */
    @Override
    public ReservationDto create(ReservationDto dto, String tenantId) {
        Book book = bookRepo.findById(dto.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Reservation r = new Reservation();
        r.setBook(book);
        r.setUser(user);
        r.setLoanDate(dto.loanDate());
        r.setDueDate(dto.dueDate());
        r.setReturned(dto.returned());
        try {
            r.setStatus(ReservationStatus.valueOf(dto.status().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + dto.status());
        }
        r.setTenantId(tenantId);

        return toDto(resRepo.save(r));
    }

    /**
     * Find all reservations by user.
     *
     * @param userId   the user ID
     * @param tenantId the tenant ID
     * @return list of reservation DTOs
     */
    @Override
    public List<ReservationDto> findByUser(Long userId, String tenantId) {
        return resRepo.findByUserIdAndTenantId(userId, tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Cancel a reservation.
     *
     * @param reservationId the reservation ID
     * @param tenantId      the tenant ID
     * @throws ResponseStatusException if reservation not found
     */
    @Override
    public void cancel(Long reservationId, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(reservationId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        resRepo.delete(r);
    }

    /**
     * Get all reservations for a tenant.
     *
     * @param tenantId the tenant ID
     * @return list of reservation details DTOs
     */
    @Override
    public List<ReservationDetailsDto> getAll(String tenantId) {
        return resRepo.findByTenantId(tenantId)
                .stream()
                .map(this::toDetailsDto)
                .collect(Collectors.toList());
    }

    /**
     * Get reservations by member.
     *
     * @param memberId the member ID
     * @param tenantId the tenant ID
     * @return list of reservation details DTOs
     */
    @Override
    public List<ReservationDetailsDto> getByMember(Long memberId, String tenantId) {
        return resRepo.findByUserIdAndTenantId(memberId, tenantId)
                .stream()
                .map(this::toDetailsDto)
                .collect(Collectors.toList());
    }

    /**
     * Update only the status of a reservation.
     *
     * @param id        the reservation ID
     * @param newStatus the new status
     * @param tenantId  the tenant ID
     * @return the updated reservation details DTO
     */
    @Override
    @Transactional
    public ReservationDetailsDto updateStatus(Long id, String newStatus, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        try {
            ReservationStatus status = ReservationStatus.valueOf(newStatus.toUpperCase());
            r.setStatus(status);
            return toDetailsDto(resRepo.save(r));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + newStatus);
        }
    }

    /**
     * Confirm pickup (approve) of a reservation:
     * decrement book quantity, create a loan, then cancel reservation.
     *
     * @param id       the reservation ID
     * @param tenantId the tenant ID
     * @return the reservation details DTO
     * @throws ResponseStatusException on invalid status or out of stock
     */
    @Override
    @Transactional
    public ReservationDetailsDto confirmPickup(Long id, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        if (r.getStatus() != ReservationStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation must be approved before pickup.");
        }

        Book book = r.getBook();
        if (book.getQuantity() > 0) {
            book.setQuantity(book.getQuantity() - 1);
            bookRepo.save(book);

            loanService.createLoanFromReservation(r.getId(), tenantId);
            r.setStatus(ReservationStatus.CANCELLED);
            resRepo.save(r);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is out of stock");
        }

        return toDetailsDto(r);
    }

    /**
     * Check if a book is available for reservation for a given user.
     *
     * @param bookId   the book ID
     * @param userId   the user ID
     * @param tenantId the tenant ID
     * @return true if available, false otherwise
     */
    @Override
    public boolean checkAvailability(Long bookId, Long userId, String tenantId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        int quantity = book.getQuantity();
        boolean userHasRes = resRepo.existsByBookIdAndUserIdAndReturnedFalseAndTenantId(bookId, userId, tenantId);
        int activeRes = resRepo.findByBookIdAndReturnedFalseAndTenantId(bookId, tenantId).size();
        int activeLoans = 0;

        return quantity > (activeRes + activeLoans) && !userHasRes;
    }

    /**
     * Helper to convert Reservation to ReservationDto.
     *
     * @param r the reservation entity
     * @return the reservation DTO
     */
    private ReservationDto toDto(Reservation r) {
        return new ReservationDto(
                r.getId(),
                r.getBook().getId(),
                r.getUser().getId(),
                r.getLoanDate(),
                r.getDueDate(),
                r.isReturned(),
                r.getStatus().name()
        );
    }

    /**
     * Helper to convert Reservation to ReservationDetailsDto.
     *
     * @param r the reservation entity
     * @return the reservation details DTO
     */
    private ReservationDetailsDto toDetailsDto(Reservation r) {
        return new ReservationDetailsDto(
                r.getId(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getBook().getAuthor().getName(),
                r.getUser().getId(),
                r.getUser().getFullName(),
                r.getLoanDate(),
                r.getDueDate(),
                r.getStatus().name().toLowerCase()
        );
    }
}
