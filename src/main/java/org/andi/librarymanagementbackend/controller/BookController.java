// src/main/java/org/andi/librarymanagementbackend/controller/BookController.java
package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing books.
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Retrieve all books.
     *
     * @param tenantId X-Tenant-ID header
     * @return 200 OK with list of BookDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Retrieve a book by its ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the book ID
     * @return 200 OK with the BookDto
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Create a new book.
     *
     * @param tenantId X-Tenant-ID header
     * @param bookDto  the book data
     * @return 200 OK with the created BookDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<BookDto> createBook(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody BookDto bookDto) {
        BookDto created = bookService.createBook(bookDto);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing book.
     *
     * @param tenantId X-Tenant-ID header
     * @param id        the ID of the book to update
     * @param bookDto   the updated data
     * @return 200 OK with the updated BookDto
     */
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody BookDto bookDto) {
        BookDto updated = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a book by its ID.
     *
     * @param tenantId X-Tenant-ID header
     * @param id       the book ID
     * @return 204 No Content on success
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
