package com.biblioteca.usuarios.exception;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(Long id) {
        super("El usuario con id " + id + " no está activo");
    }
}
