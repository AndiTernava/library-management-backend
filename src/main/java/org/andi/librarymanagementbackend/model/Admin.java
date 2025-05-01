package org.andi.librarymanagementbackend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    private String adminCode;

    // Constructors
    public Admin() {
        super();
    }

    public Admin(String fullName, String email, String password, Role role, String adminCode) {
        super(fullName, email, password, role);
        this.adminCode = adminCode;
    }

    // Getters and Setters
    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }
}
