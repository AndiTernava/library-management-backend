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
import org.andi.librarymanagementbackend.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository resRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public ReservationServiceImpl(ReservationRepository resRepo,
                                  BookRepository bookRepo,
                                  UserRepository userRepo) {
        this.resRepo = resRepo;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
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
    public ReservationDetailsDto updateStatus(Long id, String newStatus, String tenantId) {
        Reservation r = resRepo.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        try {
            r.setStatus(ReservationStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + newStatus);
        }
        return toDetailsDto(resRepo.save(r));
    }

    @Override
    public boolean checkAvailability(Long bookId, String tenantId) {
        return !resRepo.existsByBookIdAndReturnedFalseAndTenantId(bookId, tenantId);
    }

    // ────────────────────────────────────────────────────────
    // mapper helpers
    // ────────────────────────────────────────────────────────

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

