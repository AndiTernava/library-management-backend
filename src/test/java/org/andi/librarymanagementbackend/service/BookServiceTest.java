package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldReturnAllBooks() {
        List<Book> books = List.of(new Book("Book1", "ISBN123", 5, null, null, null));
        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Book1", result.get(0).getTitle());
    }
}
