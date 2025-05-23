package org.andi.librarymanagementbackend.api;




import com.fasterxml.jackson.databind.ObjectMapper;
import org.andi.librarymanagementbackend.config.TenantFilterInterceptor;
import org.andi.librarymanagementbackend.controller.BookController;
import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
// disable ALL filters (security + tenant) so we don't need CSRF or X-Tenant-ID
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean BookService bookService;
    @MockBean TenantFilterInterceptor tenantFilterInterceptor;  // mock out your tenant filter

    private static final String TENANT = "tenant1";

    @Test
    @DisplayName("GET /api/books → list of books")
    @WithMockUser
    void getAllBooks_ReturnsList() throws Exception {
        BookDto b1 = new BookDto(1L, "Title1", "ISBN1", 5, 2L,3L,4L);
        given(bookService.getAllBooks()).willReturn(List.of(b1));

        mockMvc.perform(get("/api/books")
                        .header("X-Tenant-ID", TENANT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[0].isbn").value("ISBN1"))
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    @Test
    @DisplayName("GET /api/books/{id} → single book")
    @WithMockUser
    void getBookById_ReturnsBook() throws Exception {
        BookDto b = new BookDto(5L, "Book5", "ISBN5", 10, 2L,3L,4L);
        given(bookService.getBookById(5L)).willReturn(b);

        mockMvc.perform(get("/api/books/{id}", 5)
                        .header("X-Tenant-ID", TENANT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Book5"))
                .andExpect(jsonPath("$.isbn").value("ISBN5"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("POST /api/books → create book (librarian)")
    @WithMockUser(roles = "LIBRARIAN")
    void createBook_AsLibrarian_ReturnsCreated() throws Exception {
        BookDto input = new BookDto(null, "NewBook", "NEWISBN", 1, 2L,3L,4L);
        BookDto created = new BookDto(10L, "NewBook", "NEWISBN", 1, 2L,3L,4L);
        given(bookService.createBook(any(BookDto.class))).willReturn(created);

        mockMvc.perform(post("/api/books")
                        .header("X-Tenant-ID", TENANT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("NewBook"));
    }

    @Test
    @DisplayName("PUT /api/books/{id} → update book (librarian)")
    @WithMockUser(roles = "LIBRARIAN")
    void updateBook_AsLibrarian_ReturnsUpdated() throws Exception {
        BookDto input = new BookDto(null, "Updated", "UPISBN", 2, 2L,3L,4L);
        BookDto updated = new BookDto(20L, "Updated", "UPISBN", 2, 2L,3L,4L);
        given(bookService.updateBook(eq(20L), any(BookDto.class))).willReturn(updated);

        mockMvc.perform(put("/api/books/{id}", 20)
                        .header("X-Tenant-ID", TENANT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} → no content (admin)")
    @WithMockUser(roles = "ADMIN")
    void deleteBook_AsAdmin_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", 30)
                        .header("X-Tenant-ID", TENANT))
                .andExpect(status().isNoContent());
    }
}
