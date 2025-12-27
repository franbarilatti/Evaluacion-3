package com.biblioteca.prestamos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookStockDTO {
    private Long id;
    private String title;
    private Integer availableCopies;
    private boolean available;
}
