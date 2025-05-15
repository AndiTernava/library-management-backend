package org.andi.librarymanagementbackend.controller;

import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET all books
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        // opcional: mund ta përdorim tenantId në log, auditing, ose për tenant-resolver
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // GET book by ID
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // POST create a new book
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<BookDto> createBook(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody BookDto bookDto
    ) {
        BookDto created = bookService.createBook(bookDto);
        return ResponseEntity.ok(created);
    }

    // PUT update a book
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id,
            @RequestBody BookDto bookDto
    ) {
        BookDto updated = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(updated);
    }

    // DELETE a book
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
