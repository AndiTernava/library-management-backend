// src/main/java/org/andi/librarymanagementbackend/service/impl/UserServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.mapper.UserMapper;
import org.andi.librarymanagementbackend.model.*;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.LibraryCardService;
import org.andi.librarymanagementbackend.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing users.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LibraryCardService libraryCardService;

    @PersistenceContext
    private EntityManager em;

    /**
     * Constructor.
     *
     * @param userRepository  the UserRepository
     * @param passwordEncoder the password encoder
     * @param libraryCardService the library card service
     */
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LibraryCardService libraryCardService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.libraryCardService = libraryCardService;
    }

    /**
     * Get all users.
     *
     * @return list of user DTOs
     */
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a user by ID.
     *
     * @param id the user ID
     * @return the user DTO
     */
    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Create a new user (Admin, Librarian, or Member).
     *
     * @param dto the user DTO
     * @return the created user DTO
     * @throws IllegalArgumentException if required fields missing
     */
    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        switch (user.getRole()) {
            case ADMIN -> {
                Admin a = (Admin) user;
                if (a.getAdminCode() == null || a.getAdminCode().isBlank()) {
                    throw new IllegalArgumentException("Admin code is required");
                }
            }
            case LIBRARIAN -> {
                Librarian l = (Librarian) user;
                if (l.getDepartment() == null || l.getDepartment().isBlank()) {
                    throw new IllegalArgumentException("Department is required");
                }
                if (l.getEmployeeNumber() == null || l.getEmployeeNumber().isBlank()) {
                    l.setEmployeeNumber("EMP-" + UUID.randomUUID());
                }
            }
            case MEMBER -> {
                Member m = (Member) user;
                m.setMembershipId("MEM-" + UUID.randomUUID());
            }
        }

        User saved = userRepository.save(user);

        // Create library card if the user is a member
        if (saved instanceof Member) {
            libraryCardService.createCardForUser(saved);
        }

        return UserMapper.toDto(saved);
    }

    /**
     * Update an existing user. Supports role changes and field patches.
     *
     * @param id  the user ID
     * @param dto the updated user DTO
     * @return the updated user DTO
     */
    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getRole() != null && dto.getRole() != existing.getRole()) {
            em.flush();
            userRepository.updateRole(id, dto.getRole().name());
            switch (dto.getRole()) {
                case LIBRARIAN -> {
                    String emp = (dto.getEmployeeNumber() != null && !dto.getEmployeeNumber().isBlank())
                            ? dto.getEmployeeNumber()
                            : "EMP-" + UUID.randomUUID();
                    userRepository.updateLibrarianFields(id, dto.getDepartment(), emp);
                }
                case ADMIN -> userRepository.updateAdminCode(id, dto.getAdminCode());
                case MEMBER -> userRepository.updateMemberFields(id, "MEM-" + UUID.randomUUID());
            }
            em.flush();
            em.clear();
            existing = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Role conversion failed"));
        }

        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getEmail()    != null) existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getAddress()     != null) existing.setAddress(dto.getAddress());
        if (dto.getPhoneNumber() != null) existing.setPhoneNumber(dto.getPhoneNumber());

        if (existing instanceof Librarian l) {
            if (dto.getDepartment()     != null) l.setDepartment(dto.getDepartment());
            if (dto.getEmployeeNumber() != null) l.setEmployeeNumber(dto.getEmployeeNumber());
        } else if (existing instanceof Admin a) {
            if (dto.getAdminCode() != null) a.setAdminCode(dto.getAdminCode());
        }

        User saved = userRepository.save(existing);
        return UserMapper.toDto(saved);
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user ID
     */
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
