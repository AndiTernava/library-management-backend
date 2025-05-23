// LibraryCard.java (Updated)
package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "library_card")
public class LibraryCard extends TenantBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate issuedDate;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private LibraryCardStatus status;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public LibraryCard() {}

    public LibraryCard(LocalDate issuedDate, User user) {
        this.issuedDate = issuedDate;
        this.expiryDate = issuedDate.plusYears(1);
        this.status = LibraryCardStatus.ACTIVE;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LibraryCardStatus getStatus() {
        return status;
    }

    public void setStatus(LibraryCardStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
