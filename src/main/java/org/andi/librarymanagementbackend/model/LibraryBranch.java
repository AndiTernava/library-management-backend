package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "library_branch")
public class LibraryBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "libraryBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;

    public LibraryBranch() {}

    public LibraryBranch(String branchName, String address) {
        this.branchName = branchName;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
