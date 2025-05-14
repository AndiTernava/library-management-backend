package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import org.andi.librarymanagementbackend.config.TenantEntityListener;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@DiscriminatorValue("LIBRARIAN")
public class Librarian extends User {

    private String employeeNumber;
    private String department;


    // Constructors
    public Librarian() {
        super();
    }

    public Librarian(String fullName, String email, String password, Role role,String employeeNumber, String department) {
        super(fullName, email, password, role);
        this.employeeNumber = employeeNumber;
        this.department = department;
    }

    // Getters and Setters
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
