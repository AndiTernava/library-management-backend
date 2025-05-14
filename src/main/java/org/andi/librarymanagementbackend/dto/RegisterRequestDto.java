package org.andi.librarymanagementbackend.dto;

public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;

    public RegisterRequestDto() {}

    public RegisterRequestDto(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // getters & setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

