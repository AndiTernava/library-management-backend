package org.andi.librarymanagementbackend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.andi.librarymanagementbackend.dto.*;
import org.andi.librarymanagementbackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String tenantHeader = "X-Tenant-ID";

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequestDto registerDto =
                new RegisterRequestDto(
                        "Gezim",
                        "gezim@example.com",
                        "password123",
                        "123 Main St",       // a dummy address for your test
                        "555-0123"           // a dummy phone number
                );

        AuthResponseDto responseDto = new AuthResponseDto("token123", 1L, "Gezim", "gezim@example.com", "MEMBER");

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .header(tenantHeader, "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.email").value("gezim@example.com"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("gezim@example.com", "password123");
        AuthResponseDto responseDto = new AuthResponseDto("token456", 1L, "Gezim", "gezim@example.com", "MEMBER");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/login")
                        .header(tenantHeader, "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token456"))
                .andExpect(jsonPath("$.fullName").value("Gezim"));
    }
}

