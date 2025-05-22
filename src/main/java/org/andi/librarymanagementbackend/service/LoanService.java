package org.andi.librarymanagementbackend.service;

import org.andi.librarymanagementbackend.dto.LoanDto;
import java.util.List;

public interface LoanService {
    List<LoanDto> getActiveLoans(String tenantId);
    List<LoanDto> getLoanHistory(String tenantId);
    LoanDto returnLoan(Long loanId, String tenantId);
    LoanDto createLoanFromReservation(Long reservationId, String tenantId);
}