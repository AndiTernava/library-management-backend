// src/main/java/org/andi/librarymanagementbackend/model/Member.java
package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MEMBER")
public class Member extends User {

    @Column(nullable = false, unique = true)
    private String membershipId;

    public Member() { super(); }

    public Member(String fullName, String email, String password, Role role, String membershipId) {
        super(fullName, email, password, role);
        this.membershipId = membershipId;
    }

    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }
}
