package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.mapper.BookMapper;
import org.andi.librarymanagementbackend.model.*;
import org.andi.librarymanagementbackend.repository.*;
import org.andi.librarymanagementbackend.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           CategoryRepository categoryRepository,
                           PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Author author = authorRepository.findById(bookDto.getAuthorId()).orElseThrow();
        Category category = categoryRepository.findById(bookDto.getCategoryId()).orElseThrow();
        Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow();

        Book book = BookMapper.toEntity(bookDto, author, category, publisher);
        return BookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow();
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book existing = bookRepository.findById(id).orElseThrow();

        Author author = authorRepository.findById(bookDto.getAuthorId()).orElseThrow();
        Category category = categoryRepository.findById(bookDto.getCategoryId()).orElseThrow();
        Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow();

        existing.setTitle(bookDto.getTitle());
        existing.setIsbn(bookDto.getIsbn());
        existing.setQuantity(bookDto.getQuantity());
        existing.setAuthor(author);
        existing.setCategory(category);
        existing.setPublisher(publisher);

        return BookMapper.toDto(bookRepository.save(existing));
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
