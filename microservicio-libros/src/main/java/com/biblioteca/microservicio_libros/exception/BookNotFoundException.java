package com.biblioteca.microservicio_libros.exception;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(Long id){
        super("No se encontró el libro con id: "+ id);
    }
}
