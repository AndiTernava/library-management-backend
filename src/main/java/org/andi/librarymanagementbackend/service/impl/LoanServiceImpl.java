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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private final BookRepository         bookRepo;
    private final LoanHistoryRepository  loanRepo;
    private final ReservationRepository  reservationRepo;
    private final FineService            fineService;

    public LoanServiceImpl(LoanHistoryRepository loanRepo,
                           ReservationRepository reservationRepo,
                           BookRepository bookRepo,
                           FineService fineService) {
        this.loanRepo        = loanRepo;
        this.reservationRepo = reservationRepo;
        this.bookRepo        = bookRepo;
        this.fineService     = fineService;
    }

    @Override
    public List<LoanDto> getActiveLoans(String tenantId) {
        return loanRepo.findByTenantIdAndReturnedFalse(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanDto> getLoanHistory(String tenantId) {
        return loanRepo.findByTenantId(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mark a loan as returned. If it's returned after the due date,
     * set returnStatus to LATE and issue a $10 fine.
     */
    @Override
    @Transactional
    public LoanDto returnLoan(Long loanId, String tenantId) {
        LoanHistory loan = loanRepo.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found: " + loanId));

        // 1) mark returned
        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanHistory.LoanStatus.RETURNED);

        // 2) determine on-time vs late
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setReturnStatus(LoanHistory.ReturnStatus.LATE);
            // 3) issue the $10 fine
            fineService.applyFine(loan.getUser().getId());
        } else {
            loan.setReturnStatus(LoanHistory.ReturnStatus.ON_TIME);
        }

        // 4) increment book quantity
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepo.save(book);

        // 5) persist and return DTO
        LoanHistory updated = loanRepo.save(loan);
        return toDto(updated);
    }

    /**
     * Create a new loan record from a reservation. (Quantity was decremented
     * earlier in your ReservationServiceImpl.confirmPickup(...) method.)
     */
    @Override
    @Transactional
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

    // ─── Helper to map entity → DTO ────────────────────────────────
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
