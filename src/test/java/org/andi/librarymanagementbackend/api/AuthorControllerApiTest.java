package org.andi.librarymanagementbackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.andi.librarymanagementbackend.config.JwtAuthenticationFilter;
import org.andi.librarymanagementbackend.config.TenantFilterInterceptor;
import org.andi.librarymanagementbackend.controller.AuthorController;
import org.andi.librarymanagementbackend.dto.AuthorDto;
import org.andi.librarymanagementbackend.service.AuthorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API‐slice tests for AuthorController.
 */
@WebMvcTest(controllers = AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock your service and any extra filters/interceptors
    @MockBean
    private AuthorService authorService;

    @MockBean
    private TenantFilterInterceptor tenantFilterInterceptor;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Nested
    @DisplayName("GET /api/authors")
    class GetAllAuthors {
        @Test
        @WithMockUser            // any authenticated user
        void returnsListOfAuthors() throws Exception {
            // Arrange
            List<AuthorDto> authors = List.of(
                    new AuthorDto(1L, "Jane Austen"),
                    new AuthorDto(2L, "Mark Twain")
            );
            given(authorService.getAllAuthors()).willReturn(authors);

            // Act & Assert
            mockMvc.perform(get("/api/authors")
                            .header("X-Tenant-ID", "tenant123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()", is(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("Jane Austen")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].name", is("Mark Twain")));
        }
    }

    @Nested
    @DisplayName("GET /api/authors/{id}")
    class GetAuthorById {
        @Test
        @WithMockUser
        void returnsSingleAuthor() throws Exception {
            // Arrange
            var dto = new AuthorDto(5L, "George Orwell");
            given(authorService.getAuthorById(5L)).willReturn(dto);

            // Act & Assert
            mockMvc.perform(get("/api/authors/{id}", 5L)
                            .header("X-Tenant-ID", "tenant123")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.name", is("George Orwell")));
        }
    }

    @Nested
    @DisplayName("POST /api/authors")
    class CreateAuthor {
        @Test
        @WithMockUser(roles = "LIBRARIAN")   // must be LIBRARIAN or ADMIN
        void createsAndReturnsAuthor() throws Exception {
            // Arrange
            var input = new AuthorDto(null, "Isaac Asimov");
            var saved = new AuthorDto(10L, "Isaac Asimov");
            given(authorService.createAuthor(input)).willReturn(saved);

            // Act & Assert
            mockMvc.perform(post("/api/authors")
                            .header("X-Tenant-ID", "tenant123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(10)))
                    .andExpect(jsonPath("$.name", is("Isaac Asimov")));
        }
    }

    @Nested
    @DisplayName("PUT /api/authors/{id}")
    class UpdateAuthor {
        @Test
        @WithMockUser(roles = "LIBRARIAN")
        void updatesAndReturnsAuthor() throws Exception {
            // Arrange
            var update = new AuthorDto(5L, "George R. R. Martin");
            given(authorService.updateAuthor(5L, update)).willReturn(update);

            // Act & Assert
            mockMvc.perform(put("/api/authors/{id}", 5L)
                            .header("X-Tenant-ID", "tenant123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(5)))
                    .andExpect(jsonPath("$.name", is("George R. R. Martin")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/authors/{id}")
    class DeleteAuthor {
        @Test
        @WithMockUser(roles = "ADMIN")     // must be ADMIN
        void deletesAuthor() throws Exception {
            // Arrange
            willDoNothing().given(authorService).deleteAuthor(7L);

            // Act & Assert
            mockMvc.perform(delete("/api/authors/{id}", 7L)
                            .header("X-Tenant-ID", "tenant123"))
                    .andExpect(status().isNoContent());
        }
    }
}