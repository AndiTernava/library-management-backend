// LibraryCardRepository.java
package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {
    LibraryCard findByUserId(Long userId);
}