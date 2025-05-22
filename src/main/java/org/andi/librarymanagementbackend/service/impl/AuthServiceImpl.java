package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.config.JwtUtil;
import org.andi.librarymanagementbackend.dto.AuthResponseDto;
import org.andi.librarymanagementbackend.dto.LoginRequestDto;
import org.andi.librarymanagementbackend.dto.RegisterRequestDto;
import org.andi.librarymanagementbackend.model.Member;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto req) {
        // 1. Prevent duplicate emails
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // 2. Build Member entity first
        Member m = new Member();
        m.setFullName(   req.getFullName());
        m.setEmail(      req.getEmail());
        m.setPassword(   passwordEncoder.encode(req.getPassword()));
        m.setRole(       User.Role.MEMBER);
        m.setAddress(    req.getAddress());
        m.setPhoneNumber(req.getPhoneNumber());

        // 3. Now generate a simple 5-digit membershipId that starts with '1'
        int idNum = ThreadLocalRandom.current().nextInt(10000, 20000);
        String membershipId = String.valueOf(idNum);
        m.setMembershipId(membershipId);

        // 4. Persist to database
        User saved = userRepository.save(m);

        // 5. Generate JWT and return response DTO
        String token = jwtUtil.generateToken(saved.getEmail());
        return new AuthResponseDto(
                token,
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }

    @Override
    public AuthResponseDto login(LoginRequestDto req) {
        // 1. Lookup user
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. Verify password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate JWT and return response
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDto(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
