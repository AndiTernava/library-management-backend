package org.andi.librarymanagementbackend.service;



import org.andi.librarymanagementbackend.dto.ReviewDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.Review;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.ReviewRepository;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private ReviewServiceImpl svc;

    private static final String TENANT = "tenant1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void findAll_returnsAllDtos() {
        // given
        Review r1 = makeReview(1L, 11L, 21L, "Bob",   "Good", 4);
        Review r2 = makeReview(2L, 12L, 22L, "Carol", "Okay", 3);

        when(reviewRepo.findByTenantId(TENANT))
                .thenReturn(List.of(r1, r2));

        // when
        List<ReviewDto> dtos = svc.findAll(TENANT);

        // then
        assertThat(dtos).hasSize(2)
                .extracting(ReviewDto::id)
                .containsExactly(1L, 2L);
    }

    @Test
    void findMine_returnsOnlyThatUser() {
        // given
        Long me = 21L;
        Review mine = makeReview(1L, 11L, me, "Bob", "Nice", 5);
        when(reviewRepo.findByUserIdAndTenantId(me, TENANT))
                .thenReturn(List.of(mine));

        // when
        List<ReviewDto> dtos = svc.findMine(TENANT, me);

        // then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).userId()).isEqualTo(me);
    }

    @Test
    void findByBook_returnsOnlyThatBook() {
        // given
        Long bookId = 11L;
        Review byBook = makeReview(1L, bookId, 21L, "Bob", "Nice", 5);
        when(reviewRepo.findByBookIdAndTenantId(bookId, TENANT))
                .thenReturn(List.of(byBook));

        // when
        List<ReviewDto> dtos = svc.findByBook(bookId, TENANT);

        // then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).bookId()).isEqualTo(bookId);
    }

    // helper to build a Review entity
    private Review makeReview(Long id, Long bookId, Long userId, String userFullName, String comment, int rating) {
        Book b = new Book();
        b.setId(bookId);
        b.setTenantId(TENANT);

        User u = new User();
        u.setId(userId);
        u.setFullName(userFullName);
        u.setTenantId(TENANT);

        Review r = new Review();
        r.setId(id);
        r.setBook(b);
        r.setUser(u);
        r.setComment(comment);
        r.setRating(rating);
        r.setTenantId(TENANT);
        return r;
    }
}
