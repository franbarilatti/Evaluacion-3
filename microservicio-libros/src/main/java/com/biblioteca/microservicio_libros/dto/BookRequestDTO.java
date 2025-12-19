package com.biblioteca.microservicio_libros.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    @NotBlank(message = "El título no puede estar vacío")
    private String title;

    @NotBlank(message = "El autor no puede estar vacío")
    private String author;

    @NotBlank(message = "El ISBN no puede estar vacío")
    private String isbn;

    @Min(value = 0, message = "Debe haber al menos 0 ejemplares")
    private Integer availableCopies;
}
