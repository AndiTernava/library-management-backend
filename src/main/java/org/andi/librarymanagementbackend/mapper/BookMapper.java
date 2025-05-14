package org.andi.librarymanagementbackend.mapper;

import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.model.*;

public class BookMapper {
    public static BookDto toDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setQuantity(book.getQuantity());
        dto.setAuthorId(book.getAuthor() != null ? book.getAuthor().getId() : null);
        dto.setCategoryId(book.getCategory() != null ? book.getCategory().getId() : null);
        dto.setPublisherId(book.getPublisher() != null ? book.getPublisher().getId() : null);
        return dto;
    }

    public static Book toEntity(BookDto dto, Author author, Category category, Publisher publisher) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setQuantity(dto.getQuantity());
        book.setAuthor(author);
        book.setCategory(category);
        book.setPublisher(publisher);
        return book;
    }
}