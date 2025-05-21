package org.andi.librarymanagementbackend.model;

import jakarta.persistence.*;
import org.andi.librarymanagementbackend.config.TenantEntityListener;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Entity
@Table(name = "book")
public class Book  extends  TenantBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int quantity;


    @ManyToOne
    @JoinColumn(name = "library_branch_id")
    private LibraryBranch libraryBranch;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    // Constructors
    public Book() {}

    public Book(String title, String isbn, int quantity, Author author, Category category, Publisher publisher) {
        this.title = title;
        this.isbn = isbn;
        this.quantity = quantity;
        this.author = author;
        this.category = category;
        this.publisher = publisher;

    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public Author getAuthor() {
        return author;
    }

    public Category getCategory() {
        return category;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

}
