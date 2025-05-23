package org.andi.librarymanagementbackend.unit;

import org.andi.librarymanagementbackend.config.TenantFilterInterceptor;
import org.andi.librarymanagementbackend.controller.BookController;
import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TenantFilterInterceptor.class)
})
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private final String tenantHeader = "X-Tenant-ID";

    @Test
    void shouldReturnListOfBooks() throws Exception {
        BookDto book = new BookDto(1L, "Clean Code", "9780132350884", 10, 1L, 2L, 3L);
        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books")
                        .header(tenantHeader, "tenant1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        BookDto book = new BookDto(1L, "Clean Code", "9780132350884", 10, 1L, 2L, 3L);
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1")
                        .header(tenantHeader, "tenant1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780132350884"));
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookDto book = new BookDto(null, "Refactoring", "9780201485677", 5, 1L, 2L, 3L);
        BookDto created = new BookDto(1L, "Refactoring", "9780201485677", 5, 1L, 2L, 3L);

        when(bookService.createBook(any(BookDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/books")
                        .header(tenantHeader, "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Refactoring"));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookDto updated = new BookDto(1L, "Updated Title", "123", 7, 1L, 2L, 3L);
        when(bookService.updateBook(eq(1L), any(BookDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/books/1")
                        .header(tenantHeader, "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1")
                        .header(tenantHeader, "tenant1"))
                .andExpect(status().isNoContent());
    }
}