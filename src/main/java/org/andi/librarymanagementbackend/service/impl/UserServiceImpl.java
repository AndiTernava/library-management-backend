package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.mapper.UserMapper;
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
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Map DTO → Entity (but preserve ID)
        User updatedUser = UserMapper.toEntity(userDto);
        updatedUser.setId(existingUser.getId());
        // If the DTO contains a new password, encode it. Otherwise preserve old hash:
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }
        updatedUser = userRepository.save(updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
