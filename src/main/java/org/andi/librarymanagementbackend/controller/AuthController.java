package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AuthResponseDto;
import org.andi.librarymanagementbackend.dto.LoginRequestDto;
import org.andi.librarymanagementbackend.dto.RegisterRequestDto;
import org.andi.librarymanagementbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     *
     * @param tenantId X-Tenant-ID header
     * @param dto      registration details
     * @return 200 OK with JWT and user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    /**
     * Log in an existing user.
     *
     * @param tenantId X-Tenant-ID header
     * @param dto      login credentials
     * @return 200 OK with JWT and user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
