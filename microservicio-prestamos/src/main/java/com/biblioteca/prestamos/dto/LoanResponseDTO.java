package com.biblioteca.prestamos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private Long id;
    private Long userId;
    private Long bookId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate loanDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
}
