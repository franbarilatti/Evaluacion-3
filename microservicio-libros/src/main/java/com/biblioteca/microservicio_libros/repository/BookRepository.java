package com.biblioteca.microservicio_libros.repository;

import com.biblioteca.microservicio_libros.model.Book;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository {

    Optional<Book> findByIsbn(String isbn);
    boolean existByIsbn(String isbn);
}
