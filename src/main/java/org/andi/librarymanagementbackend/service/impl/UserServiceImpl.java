package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.mapper.UserMapper;
import org.andi.librarymanagementbackend.model.Admin;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
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
    public UserDto createUser(UserDto userDto) {
        // Map DTO → Entity
        User user = UserMapper.toEntity(userDto);
        // Encode the raw password before saving
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only overwrite the fields that were provided in the DTO:
        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getEmail()    != null) existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRole() != null) {
            existing.setRole(dto.getRole());
        }

        // Handle subtype‐specific extra fields:
        if (existing instanceof Admin admin && dto.getExtraField1() != null) {
            admin.setAdminCode(dto.getExtraField1());
        }
        // …and similarly for Librarian / Member

        User saved = userRepository.save(existing);
        return UserMapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
