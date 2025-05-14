package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContaining(String title);
}