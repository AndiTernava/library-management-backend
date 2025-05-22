
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.ReservationDto;
import org.andi.librarymanagementbackend.dto.ReservationDetailsDto;
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

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository resRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final LoanService loanService;

    public ReservationServiceImpl(ReservationRepository resRepo,
                                  BookRepository bookRepo,
                                  UserRepository userRepo,
                                  LoanService loanService) {
        this.resRepo = resRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.loanService = loanService;
    }

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

    @Override
    public List<ReservationDto> findByUser(Long userId, String tenantId) {
        return resRepo.findByUserIdAndTenantId(userId, tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancel(Long reservationId, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(reservationId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        resRepo.delete(r);
    }

    @Override
    public List<ReservationDetailsDto> getAll(String tenantId) {
        return resRepo.findByTenantId(tenantId).stream()
                .map(this::toDetailsDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDetailsDto> getByMember(Long memberId, String tenantId) {
        return resRepo.findByUserIdAndTenantId(memberId, tenantId).stream()
                .map(this::toDetailsDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ReservationDetailsDto updateStatus(Long id, String newStatus, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        try {
            ReservationStatus status = ReservationStatus.valueOf(newStatus.toUpperCase());
            r.setStatus(status);

            // Only update status here. Do NOT decrement quantity or create loan yet.
            return toDetailsDto(resRepo.save(r));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + newStatus);
        }
    }


    // In implementation:
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

            // Create a loan entry for this reservation
            loanService.createLoanFromReservation(r.getId(), tenantId);

            // Optionally, set reservation status to COMPLETED or similar
            r.setStatus(ReservationStatus.CANCELLED); // or COMPLETED if you add that status
            resRepo.save(r);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is out of stock");
        }

        return toDetailsDto(r);
    }


   /* @Override
    @Transactional
    public ReservationDetailsDto updateStatus(Long id, String newStatus, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        try {
            ReservationStatus status = ReservationStatus.valueOf(newStatus.toUpperCase());
            r.setStatus(status);

            // Update book quantity and create loan if approved
            if (status == ReservationStatus.APPROVED) {
                Book book = r.getBook();
                if (book.getQuantity() > 0) {
                    book.setQuantity(book.getQuantity() - 1);
                    bookRepo.save(book);

                    // ✅ Create a loan entry for this reservation
                    loanService.createLoanFromReservation(r.getId(), tenantId);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is out of stock");
                }
            }

            return toDetailsDto(resRepo.save(r));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + newStatus);
        }
    }

*/
   /* @Override
    public boolean checkAvailability(Long bookId, String tenantId) {
      *//*  return !resRepo.existsByBookIdAndReturnedFalseAndTenantId(bookId, tenantId);*//*
        return true;
    }*/


   public boolean checkAvailability(Long bookId, Long userId, String tenantId) {
       Book book = bookRepo.findById(bookId)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

       int quantity = book.getQuantity();

       // 1. Prevent same user from reserving the same book twice
       boolean userHasReservation = resRepo.existsByBookIdAndUserIdAndReturnedFalseAndTenantId(bookId, userId, tenantId);
       if (userHasReservation) {
           return false;
       }

       // 2. Check total active reservations
       int activeReservations = resRepo.findByBookIdAndReturnedFalseAndTenantId(bookId, tenantId).size();

       // 3. (Optional) Add active loans count if you want
       int activeLoans = 0;
       // activeLoans = loanRepo.countByBookIdAndReturnedFalseAndTenantId(bookId, tenantId);

       return quantity > (activeReservations + activeLoans);
   }





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