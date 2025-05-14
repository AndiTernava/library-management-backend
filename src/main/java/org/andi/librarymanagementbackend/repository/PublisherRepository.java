package org.andi.librarymanagementbackend.repository;

import org.andi.librarymanagementbackend.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
