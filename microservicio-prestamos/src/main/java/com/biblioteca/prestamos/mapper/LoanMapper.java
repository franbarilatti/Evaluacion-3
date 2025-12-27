package com.biblioteca.prestamos.mapper;

import com.biblioteca.prestamos.dto.LoanRequestDTO;
import com.biblioteca.prestamos.dto.LoanResponseDTO;
import com.biblioteca.prestamos.model.Loan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoanMapper {

    public Loan toEntity(LoanRequestDTO dto) {
        Loan loan = new Loan();
        loan.setUserId(dto.getUserId());
        loan.setBookId(dto.getBookId());
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(null);
        return loan;
    }

    public LoanResponseDTO toResponseDTO(Loan loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getUserId(),
                loan.getBookId(),
                loan.getLoanDate(),
                loan.getReturnDate()
        );
    }
}
