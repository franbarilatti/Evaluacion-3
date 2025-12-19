package com.biblioteca.microservicio_libros.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookStockDTO {
    private Long id;
    private String title;
    private Integer availableCopies;
    private boolean available;
}
