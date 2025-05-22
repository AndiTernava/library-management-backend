// src/main/java/org.andi/librarymanagementbackend/service/ReservationService.java
package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.ReservationDetailsDto;
import org.andi.librarymanagementbackend.dto.ReservationDto;
import java.util.List;

public interface ReservationService {
    ReservationDto create(ReservationDto dto, String tenantId);
    List<ReservationDto> findByUser(Long userId, String tenantId);
    void cancel(Long reservationId, String tenantId);

    List<ReservationDetailsDto> getAll(String tenantId);
    List<ReservationDetailsDto> getByMember(Long memberId, String tenantId);
    ReservationDetailsDto updateStatus(Long id, String newStatus, String tenantId);
   /* boolean checkAvailability(Long bookId, String tenantId);*/
    boolean checkAvailability(Long bookId, Long userId, String tenantId);
    ReservationDetailsDto confirmPickup(Long id, String tenantId);
}

