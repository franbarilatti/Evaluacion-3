package com.biblioteca.microservicio_libros.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long id) {
        super("El libro con id " + id + " no tiene ejemplares disponibles");
    }
}
