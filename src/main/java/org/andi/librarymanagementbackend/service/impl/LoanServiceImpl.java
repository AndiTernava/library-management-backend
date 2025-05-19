// src/main/java/org/andi/librarymanagementbackend/service/impl/LoanServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.dto.LoanDto;
import org.andi.librarymanagementbackend.model.LoanHistory;
import org.andi.librarymanagementbackend.repository.LoanHistoryRepository;
import org.andi.librarymanagementbackend.service.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanHistoryRepository loanRepo;

    public LoanServiceImpl(LoanHistoryRepository loanRepo) {
        this.loanRepo = loanRepo;
    }

    @Override
    public List<LoanDto> getActiveLoans(String tenantId) {
        return loanRepo.findByTenantIdAndReturnedFalse(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanDto> getLoanHistory(String tenantId) {
        return loanRepo.findByTenantId(tenantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void returnLoan(Long loanId, String tenantId) {
        LoanHistory loan = loanRepo.findByIdAndTenantId(loanId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loan not found"));
        loan.setReturned(true);
        loanRepo.save(loan);
    }

    private LoanDto toDto(LoanHistory l) {
        return new LoanDto(
                l.getId(),
                l.getBook().getId(),
                l.getUser().getId(),
                l.getLoanDate(),
                l.getReturnDate(),
                l.isReturned()
        );
    }
}

