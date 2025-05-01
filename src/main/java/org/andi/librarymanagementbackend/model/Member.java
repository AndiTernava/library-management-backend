package org.andi.librarymanagementbackend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("MEMBER")
public class Member extends User {

    private String membershipId;
    private String address;
    private String phoneNumber;

    // Constructors
    public Member() {
        super();
    }

    public Member(String fullName, String email, String password, Role role, String membershipId, String address, String phoneNumber) {
        super(fullName, email, password, role);
        this.membershipId = membershipId;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
