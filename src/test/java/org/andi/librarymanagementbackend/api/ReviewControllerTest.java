package org.andi.librarymanagementbackend.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.andi.librarymanagementbackend.controller.ReviewController;
import org.andi.librarymanagementbackend.config.TenantFilterInterceptor;
import org.andi.librarymanagementbackend.dto.ReviewDto;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.security.CustomUserDetails;
import org.andi.librarymanagementbackend.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ReviewService svc;
    @MockBean private TenantFilterInterceptor tenantFilter; // stub out the tenant filter
    @MockBean private EntityManager entityManager;

    private static final String TENANT = "tenant1";

    private UsernamePasswordAuthenticationToken authToken(long id, User.Role role, String fullName) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        u.setFullName(fullName);
        CustomUserDetails details = new CustomUserDetails(u);
        return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
    }



    @Test @DisplayName("Librarian sees all reviews")
    void librarianSeesAllReviews() throws Exception {
        var r1 = new ReviewDto(2L, 11L, 9L, "Bob", "Okay", 3);
        when(svc.findAll(eq(TENANT))).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/reviews")
                        .header("X-Tenant-ID", TENANT)
                        .with(authentication(authToken(8L, User.Role.LIBRARIAN, "Bob")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].userFullName").value("Bob"));
    }

    @Test @DisplayName("Authenticated user can fetch reviews for a book")
    void userCanGetReviewsForBook() throws Exception {
        long bookId = 20L;
        var r1 = new ReviewDto(3L, bookId, 5L, "Carol", "Lovely", 4);
        when(svc.findByBook(eq(bookId), eq(TENANT))).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/reviews/book/{bookId}", bookId)
                        .header("X-Tenant-ID", TENANT)
                        .with(authentication(authToken(5L, User.Role.MEMBER, "Carol")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(20))
                .andExpect(jsonPath("$[0].userFullName").value("Carol"));
    }
}