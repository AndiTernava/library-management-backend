// src/main/java/org/andi/librarymanagementbackend/mapper/FineMapper.java
package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.FineDto;
import org.andi.librarymanagementbackend.model.Fine;

public class FineMapper {
    public static FineDto toDto(Fine f) {
        return new FineDto(
                f.getId(),
                f.getAmount(),
                f.getIssuedDate(),
                f.isPaid()
        );
    }
}
