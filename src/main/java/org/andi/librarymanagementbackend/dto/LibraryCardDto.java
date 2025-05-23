package org.andi.librarymanagementbackend.dto;

import java.time.LocalDate;

public class LibraryCardDto {

    private Long id;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private String status;
    private String fullName;
    private String membershipId;

    public LibraryCardDto() {}

    public LibraryCardDto(Long id, LocalDate issuedDate, LocalDate expiryDate, String status, String fullName, String membershipId) {
        this.id = id;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.fullName = fullName;
        this.membershipId = membershipId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }
}
