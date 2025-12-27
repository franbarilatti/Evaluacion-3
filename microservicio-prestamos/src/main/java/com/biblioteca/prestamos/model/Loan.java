package com.biblioteca.prestamos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El id del usuario es oblicatorio")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "El ID del libro es obligatorio")
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @NotNull
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(name = "return_date")
    private LocalDate returnDate;
}
