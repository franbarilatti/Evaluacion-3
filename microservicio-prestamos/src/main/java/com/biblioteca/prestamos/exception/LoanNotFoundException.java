package com.biblioteca.prestamos.exception;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("No se encontro el prestamo con id: " + id);
    }
}
