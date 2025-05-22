package org.andi.librarymanagementbackend.dto;

public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;

    // ─────────────── New fields ───────────────
    private String address;
    private String phoneNumber;

    public RegisterRequestDto() {}

    public RegisterRequestDto(
            String fullName,
            String email,
            String password,
            String address,
            String phoneNumber
    ) {
        this.fullName    = fullName;
        this.email       = email;
        this.password    = password;
        this.address     = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters & setters for existing fields
    public String getFullName()        { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword()        { return password; }
    public void setPassword(String password) { this.password = password; }

    // ───────── Getters & setters for new fields ─────────
    public String getAddress()         { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber()     { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
