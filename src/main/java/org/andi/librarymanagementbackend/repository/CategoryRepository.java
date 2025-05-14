package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
