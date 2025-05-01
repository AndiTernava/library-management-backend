package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fine")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate issuedDate;

    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Fine() {}

    public Fine(BigDecimal amount, LocalDate issuedDate, boolean paid, User user) {
        this.amount = amount;
        this.issuedDate = issuedDate;
        this.paid = paid;
        this.user = user;
    }

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
