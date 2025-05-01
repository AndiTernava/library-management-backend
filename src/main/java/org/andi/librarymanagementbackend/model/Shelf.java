package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "shelf")
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // Example: "A1", "B2"

    private String locationDescription;

    @OneToMany(mappedBy = "shelf")
    private List<Book> books; // Optional if you map it from Book side

    // Constructors
    public Shelf() {}

    public Shelf(String code, String locationDescription) {
        this.code = code;
        this.locationDescription = locationDescription;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
