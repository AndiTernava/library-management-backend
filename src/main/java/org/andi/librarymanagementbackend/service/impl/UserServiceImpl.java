// src/main/java/org/andi/librarymanagementbackend/service/impl/UserServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.mapper.UserMapper;
import org.andi.librarymanagementbackend.model.*;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

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
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        // 1) load the row (as whatever subtype it currently is)
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) if the role is changing, do a bulk‐update to swap discriminator
        if (dto.getRole() != null && dto.getRole() != existing.getRole()) {
            // flush to push any pending changes first
            em.flush();

            // clear out all old subtype columns & set new role
            userRepository.updateRole(id, dto.getRole().name());

            // now fill in the new‐role fields
            switch (dto.getRole()) {
                case LIBRARIAN -> {
                    String emp = (dto.getEmployeeNumber() != null && !dto.getEmployeeNumber().isBlank())
                            ? dto.getEmployeeNumber()
                            : "EMP-" + UUID.randomUUID();
                    userRepository.updateLibrarianFields(id,
                            dto.getDepartment(), emp);
                }
                case ADMIN -> {
                    userRepository.updateAdminCode(id,
                            dto.getAdminCode());
                }
                case MEMBER -> {
                    userRepository.updateMemberFields(id,
                            "MEM-" + UUID.randomUUID());
                }
            }

            // clear the persistence context so the next findById
            // returns a fresh entity of the correct subclass
            em.flush();
            em.clear();

            existing = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Role conversion failed"));
        }

        // 3) apply all the always-present fields
        if (dto.getFullName()    != null) existing.setFullName(dto.getFullName());
        if (dto.getEmail()       != null) existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getAddress()     != null) existing.setAddress(dto.getAddress());
        if (dto.getPhoneNumber() != null) existing.setPhoneNumber(dto.getPhoneNumber());

        // 4) patch only the subtype fields on the now-correctly-typed instance
        if (existing instanceof Librarian l) {
            if (dto.getDepartment()     != null) l.setDepartment(dto.getDepartment());
            if (dto.getEmployeeNumber() != null) l.setEmployeeNumber(dto.getEmployeeNumber());
        } else if (existing instanceof Admin a) {
            if (dto.getAdminCode() != null) a.setAdminCode(dto.getAdminCode());
        }
        // (we never allow manual membershipId edits here)

        // 5) finally save & return
        User saved = userRepository.save(existing);
        return UserMapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
