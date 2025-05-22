// src/main/java/org/andi/librarymanagementbackend/model/Admin.java
package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(nullable = false, unique = true)
    private String adminCode;

    public Admin() { super(); }

    public Admin(String fullName, String email, String password, Role role, String adminCode) {
        super(fullName, email, password, role);
        this.adminCode = adminCode;
    }

    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }
}
