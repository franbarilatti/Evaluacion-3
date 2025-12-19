package com.biblioteca.microservicio_libros.mapper;

import com.biblioteca.microservicio_libros.dto.BookRequestDTO;
import com.biblioteca.microservicio_libros.dto.BookResponseDTO;
import com.biblioteca.microservicio_libros.dto.BookStockDTO;
import com.biblioteca.microservicio_libros.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequestDTO dto){
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setAvailableCopies(dto.getAvailableCopies());

        return book;
    }

    public BookResponseDTO toResponseDTO(Book book){
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getAvailableCopies()
        );
    }

    public BookStockDTO toStockDTO(Book book){
        return new BookStockDTO(
                book.getId(),
                book.getTitle(),
                book.getAvailableCopies(),
                book.getAvailableCopies() > 0
        );
    }


}
