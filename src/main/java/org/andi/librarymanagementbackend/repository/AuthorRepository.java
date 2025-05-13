package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
