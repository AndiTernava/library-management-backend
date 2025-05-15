package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.AuthResponseDto;
import org.andi.librarymanagementbackend.dto.LoginRequestDto;
import org.andi.librarymanagementbackend.dto.RegisterRequestDto;
import org.andi.librarymanagementbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}

