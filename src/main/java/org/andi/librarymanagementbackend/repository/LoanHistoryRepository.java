// src/main/java/org/andi/librarymanagementbackend/repository/LoanHistoryRepository.java
package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.LoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {

    /** All active (not returned) loans for the given tenant */
    List<LoanHistory> findByTenantIdAndReturnedFalse(String tenantId);

    /** All loans (history) for the given tenant */
    List<LoanHistory> findByTenantId(String tenantId);

    /** Find one loan by id *and* tenant (for security) */
    Optional<LoanHistory> findByIdAndTenantId(Long id, String tenantId);
}

