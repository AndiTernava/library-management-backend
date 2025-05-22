// src/main/java/org/andi/librarymanagementbackend/model/Librarian.java
package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("LIBRARIAN")
public class Librarian extends User {

    @Column(nullable = false, unique = true)
    private String employeeNumber;

    @Column(nullable = false)
    private String department;

    public Librarian() { super(); }

    public Librarian(String fullName, String email, String password, Role role,
                     String employeeNumber, String department) {
        super(fullName, email, password, role);
        this.employeeNumber = employeeNumber;
        this.department = department;
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
