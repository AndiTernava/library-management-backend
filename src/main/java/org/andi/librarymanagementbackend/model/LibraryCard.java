package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import org.andi.librarymanagementbackend.config.TenantEntityListener;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDate;

@Entity
@Table(name = "library_card")
public class LibraryCard extends  TenantBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate issuedDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public LibraryCard() {}

    public LibraryCard(LocalDate issuedDate, User user) {
        this.issuedDate = issuedDate;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
