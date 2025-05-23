// LibraryCardService.java
package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.LibraryCardDto;
import org.andi.librarymanagementbackend.model.User;

import java.util.List;

public interface LibraryCardService {
    LibraryCardDto getByUserId(Long userId);
    List<LibraryCardDto> getAll();
    void suspendCard(Long cardId);
    void activateCard(Long cardId);
    LibraryCardDto createCardForUser(User user);
}