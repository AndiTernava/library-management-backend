package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.AuthorDto;
import org.andi.librarymanagementbackend.mapper.AuthorMapper;
import org.andi.librarymanagementbackend.model.Author;
import org.andi.librarymanagementbackend.repository.AuthorRepository;
import org.andi.librarymanagementbackend.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDto createAuthor(AuthorDto dto) {
        Author author = AuthorMapper.toEntity(dto);
        Author saved = authorRepository.save(author);
        return AuthorMapper.toDto(saved);
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        return AuthorMapper.toDto(author);
    }

    @Override
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDto updateAuthor(Long id, AuthorDto dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        author.setName(dto.getName());
        Author updated = authorRepository.save(author);
        return AuthorMapper.toDto(updated);
    }

    @Override
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}

