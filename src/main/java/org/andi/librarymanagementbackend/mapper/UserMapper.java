// src/main/java/org/andi/librarymanagementbackend/mapper/UserMapper.java
package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.model.*;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());

        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());

        if (user instanceof Admin a) {
            dto.setAdminCode(a.getAdminCode());
        } else if (user instanceof Librarian l) {
            dto.setEmployeeNumber(l.getEmployeeNumber());
            dto.setDepartment(l.getDepartment());
        } else if (user instanceof Member m) {
            dto.setMembershipId(m.getMembershipId());
        }

        return dto;
    }

    public static User toEntity(UserDto dto) {
        User user;
        switch (dto.getRole()) {
            case ADMIN:
                user = new Admin();
                break;
            case LIBRARIAN:
                user = new Librarian();
                break;
            case MEMBER:
                user = new Member();
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + dto.getRole());
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setAddress(dto.getAddress());
        user.setPhoneNumber(dto.getPhoneNumber());

        if (user instanceof Admin a && dto.getAdminCode() != null) {
            a.setAdminCode(dto.getAdminCode());
        } else if (user instanceof Librarian l) {
            if (dto.getEmployeeNumber() != null) {
                l.setEmployeeNumber(dto.getEmployeeNumber());
            }
            l.setDepartment(dto.getDepartment());
        }
        // membershipId will be set in service

        return user;
    }
}
