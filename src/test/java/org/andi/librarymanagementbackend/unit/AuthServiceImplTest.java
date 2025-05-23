package org.andi.librarymanagementbackend.unit;

import org.andi.librarymanagementbackend.config.JwtUtil;
import org.andi.librarymanagementbackend.dto.AuthResponseDto;
import org.andi.librarymanagementbackend.dto.LoginRequestDto;
import org.andi.librarymanagementbackend.dto.RegisterRequestDto;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        userRepository   = mock(UserRepository.class);
        passwordEncoder  = mock(BCryptPasswordEncoder.class);
        jwtUtil          = mock(JwtUtil.class);
        authService      = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void testRegisterSuccess() {
        // build a RegisterRequestDto with the new required fields
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFullName("Qaza Tester");
        dto.setEmail("test@example.com");
        dto.setPassword("pass123");
        dto.setAddress("123 Test St");
        dto.setPhoneNumber("555-0000");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashed-password");
        when(jwtUtil.generateToken(dto.getEmail())).thenReturn("dummy-token");

        // simulate the saved user (id + role MEMBER)
        User savedUser = new User(dto.getFullName(), dto.getEmail(), "hashed-password", User.Role.MEMBER);
        savedUser.setId(1L);

        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        AuthResponseDto response = authService.register(dto);

        assertEquals("dummy-token",       response.getToken());
        assertEquals("test@example.com",  response.getEmail());
        assertEquals("Qaza Tester",       response.getFullName());
        assertEquals("MEMBER",            response.getRole());
        assertEquals(1L,                  response.getUserId());
    }

    @Test
    void testRegisterEmailExists() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFullName("Someone");
        dto.setEmail("taken@example.com");
        dto.setPassword("pass");
        dto.setAddress("Nowhere");
        dto.setPhoneNumber("000");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.register(dto)
        );
        assertEquals("Email already in use", ex.getMessage());
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "password123");

        User user = new User("Qaza Tester", "test@example.com", "hashed-password", User.Role.ADMIN);
        user.setId(42L);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("jwt-token");

        AuthResponseDto response = authService.login(dto);

        assertEquals("jwt-token",  response.getToken());
        assertEquals(42L,          response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("ADMIN",      response.getRole());
    }

    @Test
    void testLoginInvalidEmail() {
        LoginRequestDto dto = new LoginRequestDto("unknown@example.com", "pass");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.login(dto)
        );
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequestDto dto = new LoginRequestDto("user@example.com", "wrong-pass");

        User user = new User("Qaza Tester", "user@example.com", "correct-hash", User.Role.MEMBER);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> authService.login(dto)
        );
        assertEquals("Invalid credentials", ex.getMessage());
    }
}