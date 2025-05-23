package org.andi.librarymanagementbackend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.andi.librarymanagementbackend.dto.ReviewDto;
import org.andi.librarymanagementbackend.controller.ReviewController.CreateReviewRequest;
import org.andi.librarymanagementbackend.security.CustomUserDetails;
import org.andi.librarymanagementbackend.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService svc;

    private static final String TENANT = "tenant1";

    @Test
    @DisplayName("Member can create a review")
    void memberCanCreateReview() throws Exception {
        // Prepare request DTO
        CreateReviewRequest req = new CreateReviewRequest(100L, "Great read", 4);

        // Expected response DTO
        ReviewDto dto = new ReviewDto(
                1L,             // id
                100L,           // bookId
                7L,             // userId
                "Alice Smith",  // userFullName
                "Great read",   // comment
                4               // rating
        );

        // Mock service
        Mockito.when(svc.createReview(
                eq(100L), eq("Great read"), eq(4),
                eq(7L), eq(TENANT))
        ).thenReturn(dto);

        // Build a principal with id=7, fullName="Alice Smith"
        CustomUserDetails userDetails = new CustomUserDetails(new org.andi.librarymanagementbackend.model.User() {{
            setId(7L);
            setFullName("Alice Smith");
        }});

        mockMvc.perform(post("/api/reviews")
                        .header("X-Tenant-ID", TENANT)
                        .with(user(userDetails))                            // inject our custom principal
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dto.id()))
                .andExpect(jsonPath("$.bookId").value(dto.bookId()))
                .andExpect(jsonPath("$.userId").value(dto.userId()))
                .andExpect(jsonPath("$.userFullName").value(dto.userFullName()))
                .andExpect(jsonPath("$.comment").value(dto.comment()))
                .andExpect(jsonPath("$.rating").value(dto.rating()));
    }

    @Test
    @DisplayName("Member sees only their own reviews")
    void memberSeesOwnReviews() throws Exception {
        ReviewDto r1 = new ReviewDto(1L, 10L, 7L, "Alice Smith", "Nice", 5);
        Mockito.when(svc.findMine(eq(TENANT), eq(7L)))
                .thenReturn(List.of(r1));

        CustomUserDetails userDetails = new CustomUserDetails(new org.andi.librarymanagementbackend.model.User() {{
            setId(7L);
        }});

        mockMvc.perform(get("/api/reviews")
                        .header("X-Tenant-ID", TENANT)
                        .with(user(userDetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(r1.id()))
                .andExpect(jsonPath("$[0].userId").value(r1.userId()));
    }

    @Test
    @DisplayName("Librarian sees all reviews")
    void librarianSeesAllReviews() throws Exception {
        ReviewDto r1 = new ReviewDto(2L, 11L, 9L, "Bob Builder", "Okay", 3);
        Mockito.when(svc.findAll(eq(TENANT)))
                .thenReturn(List.of(r1));

        CustomUserDetails librarian = new CustomUserDetails(new org.andi.librarymanagementbackend.model.User() {{
            setId(8L);
        }});

        mockMvc.perform(get("/api/reviews")
                        .header("X-Tenant-ID", TENANT)
                        .with(user(librarian))  // grants ROLE_LIBRARIAN
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(r1.id()));
    }

    @Test
    @DisplayName("Authenticated user can fetch reviews for a specific book")
    void userCanGetReviewsForBook() throws Exception {
        long bookId = 20L;
        ReviewDto r1 = new ReviewDto(3L, bookId, 5L, "Carol Reader", "Lovely", 4);
        Mockito.when(svc.findByBook(eq(bookId), eq(TENANT)))
                .thenReturn(List.of(r1));

        CustomUserDetails userDetails = new CustomUserDetails(new org.andi.librarymanagementbackend.model.User() {{
            setId(5L);
        }});

        mockMvc.perform(get("/api/reviews/book/{bookId}", bookId)
                        .header("X-Tenant-ID", TENANT)
                        .with(user(userDetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(r1.id()))
                .andExpect(jsonPath("$[0].bookId").value(r1.bookId()));
    }
}