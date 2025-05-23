// LibraryCardServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.LibraryCardDto;
import org.andi.librarymanagementbackend.model.LibraryCard;

import org.andi.librarymanagementbackend.model.LibraryCardStatus;
import org.andi.librarymanagementbackend.model.Member;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.LibraryCardRepository;
import org.andi.librarymanagementbackend.service.LibraryCardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryCardServiceImpl implements LibraryCardService {

    private final LibraryCardRepository repository;

    public LibraryCardServiceImpl(LibraryCardRepository repository) {
        this.repository = repository;
    }

    @Override
    public LibraryCardDto getByUserId(Long userId) {
        LibraryCard card = repository.findByUserId(userId);
        return toDto(card);
    }

    @Override
    public LibraryCardDto createCardForUser(User user) {
        LibraryCard card = new LibraryCard(LocalDate.now(), user);
        card = repository.save(card);
        return toDto(card);
    }

    @Override
    public List<LibraryCardDto> getAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void suspendCard(Long cardId) {
        LibraryCard card = repository.findById(cardId).orElseThrow();
        card.setStatus(LibraryCardStatus.SUSPENDED);
        repository.save(card);
    }

    @Override
    public void activateCard(Long cardId) {
        LibraryCard card = repository.findById(cardId).orElseThrow();
        card.setStatus(LibraryCardStatus.ACTIVE);
        repository.save(card);
    }

    private LibraryCardDto toDto(LibraryCard card) {
        var user = card.getUser();
        String membershipId = null;

        // Check if user is a Member and get the membershipId
        if (user instanceof Member member) {
            membershipId = member.getMembershipId();
        }

        return new LibraryCardDto(
                card.getId(),
                card.getIssuedDate(),
                card.getExpiryDate(),
                card.getStatus().name(),
                user.getFullName(),
                membershipId
        );
    }
}