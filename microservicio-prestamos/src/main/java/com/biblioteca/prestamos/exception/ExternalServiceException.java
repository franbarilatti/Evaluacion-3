package com.biblioteca.prestamos.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String service, String message) {
        super("Error al comunicarse con el servicio " + service + ": " + message);
    }
}
