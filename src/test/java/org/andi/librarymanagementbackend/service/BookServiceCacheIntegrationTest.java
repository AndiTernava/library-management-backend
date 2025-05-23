package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.LibraryManagementBackendApplication;
import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.model.Author;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.Category;
import org.andi.librarymanagementbackend.model.Publisher;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration test verifying that BookService.getAllBooks()
 * is cached (subsequent calls do not hit the repository again).
 */
@SpringBootTest(classes = LibraryManagementBackendApplication.class)
@ActiveProfiles("test")
@SuppressWarnings("removal") // @MockBean is marked deprecated in Spring Boot 3.4+
public class BookServiceCacheIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    void clearBooksCache() {
        Cache booksCache = cacheManager.getCache("books");
        if (booksCache != null) {
            booksCache.clear();
        }
    }

    @Test
    void getAllBooks_calledTwice_repositoryInvokedOnlyOnce() {
        // Arrange: stub repository to return a single Book entity
        Book b = new Book();
        b.setId(1L);
        b.setTitle("Test Title");
        b.setIsbn("ISBN-123");
        b.setQuantity(5);
        // set related fields so the mapper doesn't NPE
        Author a = new Author();     a.setId(1L); a.setName("Auth");
        Category c = new Category(); c.setId(1L); c.setName("Cat");
        Publisher p = new Publisher(); p.setId(1L); p.setName("Pub");
        b.setAuthor(a);
        b.setCategory(c);
        b.setPublisher(p);

        when(bookRepository.findAll()).thenReturn(List.of(b));

        // Act #1: first call → should hit repository
        List<BookDto> first = bookService.getAllBooks();
        assertThat(first).hasSize(1)
                .extracting(BookDto::getId)
                .containsExactly(1L);
        verify(bookRepository, times(1)).findAll();

        // Act #2: second call → should come from cache (no new repo call)
        List<BookDto> second = bookService.getAllBooks();
        assertThat(second).hasSize(1)
                .extracting(BookDto::getId)
                .containsExactly(1L);
        // verify no further interactions
        verifyNoMoreInteractions(bookRepository);
    }
}
