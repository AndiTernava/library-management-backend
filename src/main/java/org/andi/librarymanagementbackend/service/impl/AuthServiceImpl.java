package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.config.JwtUtil;
import org.andi.librarymanagementbackend.dto.*;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepo,
                           BCryptPasswordEncoder passwordEncoder , JwtUtil jwtUtil ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        User user = new User(
                dto.getFullName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                User.Role.MEMBER // default role
        );
        user = userRepo.save(user);
        // generate token with jwtUtil
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDto(token, user.getId(),
                user.getFullName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        // generate token
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDto(token, user.getId(),
                user.getFullName(), user.getEmail(), user.getRole().name());
    }
}
