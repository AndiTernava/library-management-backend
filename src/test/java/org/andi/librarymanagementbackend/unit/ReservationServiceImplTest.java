package org.andi.librarymanagementbackend.unit;


import org.andi.librarymanagementbackend.dto.ReservationDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.Reservation;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.ReservationRepository;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock private ReservationRepository resRepo;
    @Mock private BookRepository       bookRepo;
    @Mock private UserRepository       userRepo;
    @InjectMocks private ReservationServiceImpl service;

    private static final String TENANT = "tenant1";

    @BeforeEach
    void init() {
        // MockitoAnnotations.openMocks(this); // not needed with @ExtendWith
    }

    @Test
    void create_happyPath() {
        // given
        Long bookId = 10L, userId = 20L;
        LocalDate loan = LocalDate.of(2025,5,1);
        LocalDate due  = loan.plusDays(7);
        ReservationDto req = new ReservationDto(
                null, bookId, userId, loan, due, false, "PENDING"
        );

        Book book = new Book(); book.setId(bookId); book.setTenantId(TENANT);
        User user = new User(); user.setId(userId); user.setTenantId(TENANT);

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // capture the saved entity and assign an ID
        when(resRepo.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });

        // when
        ReservationDto out = service.create(req, TENANT);

        // then
        assertThat(out.id()).isEqualTo(99L);
        assertThat(out.bookId()).isEqualTo(bookId);
        assertThat(out.userId()).isEqualTo(userId);
        assertThat(out.loanDate()).isEqualTo(loan);
        assertThat(out.dueDate()).isEqualTo(due);
        assertThat(out.returned()).isFalse();
        assertThat(out.status()).isEqualTo("PENDING");

        verify(resRepo).save(any(Reservation.class));
    }



    @Test
    void findByUser_returnsList() {
        // given
        Long userId = 5L;
        Book b = new Book(); b.setId(11L); b.setTenantId(TENANT);
        User u = new User(); u.setId(userId); u.setFullName("Zoe"); u.setTenantId(TENANT);

        Reservation r1 = new Reservation();
        r1.setId(1L);
        r1.setBook(b);
        r1.setUser(u);
        r1.setLoanDate(LocalDate.of(2025,1,1));
        r1.setDueDate(LocalDate.of(2025,1,8));
        r1.setReturned(false);
        r1.setStatus(Reservation.ReservationStatus.PENDING);

        when(resRepo.findByUserIdAndTenantId(userId, TENANT))
                .thenReturn(List.of(r1));

        // when
        List<ReservationDto> list = service.findByUser(userId, TENANT);

        // then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).id()).isEqualTo(1L);
        assertThat(list.get(0).userId()).isEqualTo(userId);
    }

    @Test
    void cancel_happyPath() {
        // given
        Long resId = 77L;
        Reservation r = new Reservation();
        r.setId(resId);
        r.setTenantId(TENANT);

        when(resRepo.findByIdAndTenantId(resId, TENANT))
                .thenReturn(Optional.of(r));

        // when
        service.cancel(resId, TENANT);

        // then
        verify(resRepo).delete(r);
    }
}
