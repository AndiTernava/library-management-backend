// src/main/java/org.andi/librarymanagementbackend/service/ReservationService.java
package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.ReservationDto;
import java.util.List;

public interface ReservationService {
    ReservationDto create(ReservationDto dto, String tenantId);
    List<ReservationDto> findByUser(Long userId, String tenantId);
    void cancel(Long reservationId, String tenantId);
}

