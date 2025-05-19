// src/main/java/org/andi/librarymanagementbackend/repository/ReservationRepository.java
package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** All reservations for a specific user in a tenant */
    List<Reservation> findByUserIdAndTenantId(Long userId, String tenantId);

    /** Lookup one reservation by id *and* tenant */
    Optional<Reservation> findByIdAndTenantId(Long id, String tenantId);
}
