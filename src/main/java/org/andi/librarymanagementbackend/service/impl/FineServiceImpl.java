// src/main/java/org/andi/librarymanagementbackend/service/impl/FineServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.FineDto;
import org.andi.librarymanagementbackend.mapper.FineMapper;
import org.andi.librarymanagementbackend.model.Fine;
import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.FineRepository;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.service.FineService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing fines.
 */
@Service
@Transactional
public class FineServiceImpl implements FineService {
    private static final BigDecimal FLAT_FINE = BigDecimal.valueOf(10);

    private final FineRepository fineRepo;
    private final UserRepository userRepo;

    /**
     * Constructor.
     *
     * @param fineRepo the FineRepository
     * @param userRepo the UserRepository
     */
    public FineServiceImpl(FineRepository fineRepo,
                           UserRepository userRepo) {
        this.fineRepo = fineRepo;
        this.userRepo = userRepo;
    }

    /**
     * Apply a flat fine to a user.
     *
     * @param userId the user ID
     * @return the created fine DTO
     */
    @Override
    @CacheEvict(value = "unpaidFines", key = "#userId")
    public FineDto applyFine(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Fine f = new Fine(FLAT_FINE, LocalDate.now(), false, user);
        return FineMapper.toDto(fineRepo.save(f));
    }

    /**
     * Get all unpaid fines for a user.
     *
     * @param userId the user ID
     * @return list of fine DTOs
     */
    @Override
    @Cacheable(value = "unpaidFines", key = "#userId")
    @Transactional(readOnly = true)
    public List<FineDto> getUnpaidFines(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return fineRepo.findByUserAndPaidFalse(user)
                .stream()
                .map(FineMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Mark a fine as paid.
     *
     * @param fineId the fine ID
     */
    @Override
    @CacheEvict(value = "unpaidFines", allEntries = true)
    public void markAsPaid(Long fineId) {
        Fine f = fineRepo.findById(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Fine not found: " + fineId));
        f.setPaid(true);
        fineRepo.save(f);
    }
}
