package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.BookDto;
import org.andi.librarymanagementbackend.mapper.BookMapper;
import org.andi.librarymanagementbackend.model.Author;
import org.andi.librarymanagementbackend.model.Book;
import org.andi.librarymanagementbackend.model.Category;
import org.andi.librarymanagementbackend.model.Publisher;
import org.andi.librarymanagementbackend.repository.AuthorRepository;
import org.andi.librarymanagementbackend.repository.BookRepository;
import org.andi.librarymanagementbackend.repository.CategoryRepository;
import org.andi.librarymanagementbackend.repository.PublisherRepository;
import org.andi.librarymanagementbackend.service.BookService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository       bookRepository;
    private final AuthorRepository     authorRepository;
    private final CategoryRepository   categoryRepository;
    private final PublisherRepository  publisherRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository,
                           CategoryRepository categoryRepository,
                           PublisherRepository publisherRepository) {
        this.bookRepository      = bookRepository;
        this.authorRepository    = authorRepository;
        this.categoryRepository  = categoryRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    @CacheEvict(value = "books", allEntries = true)
    public BookDto createBook(BookDto bookDto) {
        Author author     = authorRepository.findById(bookDto.getAuthorId()).orElseThrow();
        Category category = categoryRepository.findById(bookDto.getCategoryId()).orElseThrow();
        Publisher pub     = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow();

        Book book = BookMapper.toEntity(bookDto, author, category, pub);
        return BookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Cacheable(value = "book", key = "#id")
    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow();
    }

    @Override
    @Cacheable(value = "books")
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = { "books", "book" }, allEntries = true)
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book existing = bookRepository.findById(id).orElseThrow();

        existing.setTitle(bookDto.getTitle());
        existing.setIsbn(bookDto.getIsbn());
        existing.setQuantity(bookDto.getQuantity());
        existing.setAuthor(authorRepository.findById(bookDto.getAuthorId()).orElseThrow());
        existing.setCategory(categoryRepository.findById(bookDto.getCategoryId()).orElseThrow());
        existing.setPublisher(publisherRepository.findById(bookDto.getPublisherId()).orElseThrow());

        return BookMapper.toDto(bookRepository.save(existing));
    }

    @Override
    @CacheEvict(value = { "books", "book" }, allEntries = true)
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
