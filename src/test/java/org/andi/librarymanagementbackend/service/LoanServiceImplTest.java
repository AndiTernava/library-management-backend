package org.andi.librarymanagementbackend.service;


import org.andi.librarymanagementbackend.dto.LoanDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.LoanHistory;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.LoanHistoryRepository;
import org.andi.librarymanagementbackend.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanHistoryRepository repo;

    @InjectMocks
    private LoanServiceImpl service;

    @Test
    void getActiveLoans_returnsDtos() {
        // given
        String tenant = "tenant1";
        Book b = new Book();
        b.setId(10L);
        b.setTenantId(tenant);
        User u = new User();
        u.setId(20L);
        u.setTenantId(tenant);

        LoanHistory lh = new LoanHistory();
        lh.setId(1L);
        lh.setBook(b);
        lh.setUser(u);
        lh.setLoanDate(LocalDate.of(2025, 5, 1));
        lh.setReturnDate(LocalDate.of(2025, 5, 8));
        lh.setReturned(false);
        lh.setTenantId(tenant);

        when(repo.findByTenantIdAndReturnedFalse(tenant))
                .thenReturn(List.of(lh));

        // when
        List<LoanDto> dtos = service.getActiveLoans(tenant);

        // then
        assertThat(dtos).hasSize(1);
        LoanDto dto = dtos.get(0);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.bookId()).isEqualTo(10L);
        assertThat(dto.userId()).isEqualTo(20L);
        assertThat(dto.loanDate()).isEqualTo(LocalDate.of(2025, 5, 1));
        assertThat(dto.dueDate()).isEqualTo(LocalDate.of(2025, 5, 8));
        assertThat(dto.returned()).isFalse();

        verify(repo).findByTenantIdAndReturnedFalse(tenant);
    }
}
