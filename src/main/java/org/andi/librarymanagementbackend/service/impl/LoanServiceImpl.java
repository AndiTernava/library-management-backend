// src/main/java/org/andi/librarymanagementbackend/service/impl/LoanServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.LoanDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.LoanHistory;
import org.andi.librarymanagementbackend.model.Reservation;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.LoanHistoryRepository;
import org.andi.librarymanagementbackend.repository.ReservationRepository;
import org.andi.librarymanagementbackend.service.FineService;
import org.andi.librarymanagementbackend.service.LoanService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing loans.
 */
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanHistoryRepository loanRepo;
    private final ReservationRepository reservationRepo;
    private final BookRepository bookRepo;
    private final FineService fineService;

    /**
     * Constructor.
     *
     * @param loanRepo         the LoanHistoryRepository
     * @param reservationRepo  the ReservationRepository
     * @param bookRepo         the BookRepository
     * @param fineService      the FineService
     */
    public LoanServiceImpl(LoanHistoryRepository loanRepo,
                           ReservationRepository reservationRepo,
                           BookRepository bookRepo,
                           FineService fineService) {
        this.loanRepo        = loanRepo;
        this.reservationRepo = reservationRepo;
        this.bookRepo        = bookRepo;
        this.fineService     = fineService;
    }

    /**
     * Get all active loans for a tenant.
     *
     * @param tenantId the tenant ID
     * @return list of active loan DTOs
     */
    @Override
    @Cacheable(value = "activeLoans", key = "#tenantId")
    public List<LoanDto> getActiveLoans(String tenantId) {
        return loanRepo.findByTenantIdAndReturnedFalse(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get loan history for a tenant.
     *
     * @param tenantId the tenant ID
     * @return list of loan history DTOs
     */
    @Override
    @Cacheable(value = "loanHistory", key = "#tenantId")
    public List<LoanDto> getLoanHistory(String tenantId) {
        return loanRepo.findByTenantId(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mark a loan as returned. Issues a fine if late, updates book quantity,
     * and evicts related caches.
     *
     * @param loanId   the loan history ID
     * @param tenantId the tenant ID
     * @return the updated loan DTO
     * @throws ResponseStatusException if loan not found
     */
    @Override
    @Transactional
    @CacheEvict(value = { "activeLoans", "loanHistory" }, key = "#tenantId")
    public LoanDto returnLoan(Long loanId, String tenantId) {
        LoanHistory loan = loanRepo.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found: " + loanId));

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanHistory.LoanStatus.RETURNED);

        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setReturnStatus(LoanHistory.ReturnStatus.LATE);
            fineService.applyFine(loan.getUser().getId());
        } else {
            loan.setReturnStatus(LoanHistory.ReturnStatus.ON_TIME);
        }

        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepo.save(book);

        LoanHistory updated = loanRepo.save(loan);
        return toDto(updated);
    }

    /**
     * Create a loan based on an approved reservation.
     *
     * @param reservationId the reservation ID
     * @param tenantId      the tenant ID
     * @return the created loan DTO
     * @throws ResponseStatusException if reservation not found
     */
    @Override
    @Transactional
    @CacheEvict(value = { "activeLoans", "loanHistory" }, key = "#tenantId")
    public LoanDto createLoanFromReservation(Long reservationId, String tenantId) {
        Reservation reservation = reservationRepo.findByIdAndTenantId(reservationId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reservation not found: " + reservationId));

        LoanHistory loan = new LoanHistory();
        loan.setBook(reservation.getBook());
        loan.setUser(reservation.getUser());
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusWeeks(3));
        loan.setReturned(false);
        loan.setStatus(LoanHistory.LoanStatus.ACTIVE);
        loan.setReturnStatus(LoanHistory.ReturnStatus.PENDING);
        loan.setTenantId(tenantId);

        return toDto(loanRepo.save(loan));
    }

    /**
     * Helper to map LoanHistory to LoanDto.
     *
     * @param l the loan history entity
     * @return the loan DTO
     */
    private LoanDto toDto(LoanHistory l) {
        return new LoanDto(
                l.getId(),
                l.getBook().getId(),
                l.getBook().getTitle(),
                l.getBook().getAuthor().getName(),
                l.getUser().getId(),
                l.getUser().getFullName(),
                l.getLoanDate(),
                l.getDueDate(),
                l.getReturnDate(),
                l.isReturned(),
                l.getStatus().name(),
                l.getReturnStatus().name()
        );
    }
}
