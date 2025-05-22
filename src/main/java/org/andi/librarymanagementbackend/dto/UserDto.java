// src/main/java/org/andi/librarymanagementbackend/dto/UserDto.java
package org.andi.librarymanagementbackend.dto;

import org.andi.librarymanagementbackend.model.User;

public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private User.Role role;

    // always-present
    private String address;
    private String phoneNumber;

    // ADMIN only
    private String adminCode;

    // LIBRARIAN only
    private String department;
    private String employeeNumber;

    // MEMBER only (output)
    private String membershipId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }
}
