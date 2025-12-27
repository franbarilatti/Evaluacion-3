package com.biblioteca.prestamos.exception;

public class LoanAlreadyReturnedException extends RuntimeException {
    public LoanAlreadyReturnedException(Long id) {
        super("El prestamo con id " + id + " ya fue devuelto");
    }
}
