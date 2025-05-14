package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.AuthResponseDto;
import org.andi.librarymanagementbackend.dto.LoginRequestDto;
import org.andi.librarymanagementbackend.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto registerDto);
    AuthResponseDto login(LoginRequestDto loginDto);
}
