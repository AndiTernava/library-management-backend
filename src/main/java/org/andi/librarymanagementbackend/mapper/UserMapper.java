package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.UserDto;
import org.andi.librarymanagementbackend.model.*;

public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());

        if (user instanceof Admin admin) {
            dto.setExtraField1(admin.getAdminCode());
        } else if (user instanceof Librarian librarian) {
            dto.setExtraField1(librarian.getEmployeeNumber());
            dto.setExtraField2(librarian.getDepartment());
        } else if (user instanceof Member member) {
            dto.setExtraField1(member.getMembershipId());
            dto.setExtraField2(member.getPhoneNumber());
        }

        return dto;
    }

    public static User toEntity(UserDto dto) {
        return switch (dto.getRole()) {
            case ADMIN -> new Admin(dto.getFullName(), dto.getEmail(), dto.getPassword(), dto.getRole(), dto.getExtraField1());
            case LIBRARIAN -> new Librarian(dto.getFullName(), dto.getEmail(), dto.getPassword(), dto.getRole(), dto.getExtraField1(), dto.getExtraField2());
            case MEMBER -> new Member(dto.getFullName(), dto.getEmail(), dto.getPassword(), dto.getRole(), dto.getExtraField1(), null, dto.getExtraField2());
        };
    }
}