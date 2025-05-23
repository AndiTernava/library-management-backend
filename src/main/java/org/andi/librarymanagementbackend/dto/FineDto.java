// src/main/java/org/andi/librarymanagementbackend/dto/FineDto.java
package org.andi.librarymanagementbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FineDto {
    private Long id;
    private BigDecimal amount;
    private LocalDate issuedDate;
    private boolean paid;

    public FineDto() {}

    public FineDto(Long id, BigDecimal amount, LocalDate issuedDate, boolean paid) {
        this.id = id;
        this.amount = amount;
        this.issuedDate = issuedDate;
        this.paid = paid;
    }

    // ── getters & setters ──
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
