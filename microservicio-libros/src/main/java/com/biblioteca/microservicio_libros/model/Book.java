package com.biblioteca.microservicio_libros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "El ISBN es obligatorio")
    @Column(unique = true, nullable = false)
    private String isbn;

    @Min(value = 0, message = "Los ejemplares no pueden ser negativos")
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;
}
