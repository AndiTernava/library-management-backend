package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.LoanDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.LoanHistory;
import org.andi.librarymanagementbackend.model.Reservation;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.LoanHistoryRepository;
import org.andi.librarymanagementbackend.repository.ReservationRepository;
import org.andi.librarymanagementbackend.service.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private final BookRepository bookRepo;
    private final LoanHistoryRepository loanRepo;
    private final ReservationRepository reservationRepo;

    public LoanServiceImpl(LoanHistoryRepository loanRepo, ReservationRepository reservationRepo,BookRepository bookRepo) {
        this.loanRepo = loanRepo;
        this.reservationRepo = reservationRepo;
        this.bookRepo = bookRepo;
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

    @Override
    @Transactional
    public LoanDto returnLoan(Long loanId, String tenantId) {
        LoanHistory loan = loanRepo.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found"));

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanHistory.LoanStatus.RETURNED);

        // Set return status based on return date
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setReturnStatus(LoanHistory.ReturnStatus.LATE);
        } else {
            loan.setReturnStatus(LoanHistory.ReturnStatus.ON_TIME);
        }

        // Increment book quantity
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        // Save book
        // (Assuming you have BookRepository injected as bookRepo)
        bookRepo.save(book);

        return toDto(loanRepo.save(loan));
    }

  /*  @Override
    @Transactional
    public LoanDto returnLoan(Long loanId, String tenantId) {
        LoanHistory loan = loanRepo.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found"));

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanHistory.LoanStatus.RETURNED);

        // Set return status based on return date
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setReturnStatus(LoanHistory.ReturnStatus.LATE);
        } else {
            loan.setReturnStatus(LoanHistory.ReturnStatus.ON_TIME);
        }

        return toDto(loanRepo.save(loan));
    }*/

    @Override
    @Transactional
    public LoanDto createLoanFromReservation(Long reservationId, String tenantId) {
        Reservation reservation = reservationRepo.findByIdAndTenantId(reservationId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reservation not found"));

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