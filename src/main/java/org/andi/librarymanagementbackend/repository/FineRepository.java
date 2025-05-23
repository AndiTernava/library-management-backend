// src/main/java/org/andi/librarymanagementbackend/repository/FineRepository.java
package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Fine;
import org.andi.librarymanagementbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    /** find all unpaid fines for a given user */
    List<Fine> findByUserAndPaidFalse(User user);
}
