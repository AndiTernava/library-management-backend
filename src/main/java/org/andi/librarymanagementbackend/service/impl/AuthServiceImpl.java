// src/main/java/org/andi/librarymanagementbackend/service/impl/AuthServiceImpl.java
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

import java.util.concurrent.ThreadLocalRandom;

/**
 * Service implementation for authentication (register & login).
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructor.
     *
     * @param userRepository   the UserRepository
     * @param passwordEncoder  the password encoder
     * @param jwtUtil          the JWT utility
     */
    public AuthServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
    }

    /**
     * Register a new member user.
     *
     * @param req registration details
     * @return the authentication response DTO
     * @throws RuntimeException if email already in use
     */
    @Override
    public AuthResponseDto register(RegisterRequestDto req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Member m = new Member();
        m.setFullName(req.getFullName());
        m.setEmail(req.getEmail());
        m.setPassword(passwordEncoder.encode(req.getPassword()));
        m.setRole(User.Role.MEMBER);
        m.setAddress(req.getAddress());
        m.setPhoneNumber(req.getPhoneNumber());

        int idNum = ThreadLocalRandom.current().nextInt(10000, 20000);
        m.setMembershipId(String.valueOf(idNum));

        User saved = userRepository.save(m);
        String token = jwtUtil.generateToken(saved.getEmail());

        return new AuthResponseDto(
                token,
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }

    /**
     * Log in an existing user.
     *
     * @param req login credentials
     * @return the authentication response DTO
     * @throws RuntimeException if credentials are invalid
     */
    @Override
    public AuthResponseDto login(LoginRequestDto req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

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
