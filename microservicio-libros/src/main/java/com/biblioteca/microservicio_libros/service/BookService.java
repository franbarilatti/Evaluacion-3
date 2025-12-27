package com.biblioteca.microservicio_libros.service;

import com.biblioteca.microservicio_libros.dto.BookRequestDTO;
import com.biblioteca.microservicio_libros.dto.BookResponseDTO;
import com.biblioteca.microservicio_libros.dto.BookStockDTO;
import com.biblioteca.microservicio_libros.exception.BookNotFoundException;
import com.biblioteca.microservicio_libros.exception.DuplicateIsbnException;
import com.biblioteca.microservicio_libros.exception.InsufficientStockException;
import com.biblioteca.microservicio_libros.mapper.BookMapper;
import com.biblioteca.microservicio_libros.model.Book;
import com.biblioteca.microservicio_libros.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;


    @Transactional
    public BookResponseDTO createBook(BookRequestDTO requestDTO){
        if (bookRepository.existsByIsbn(requestDTO.getIsbn())){
            throw new DuplicateIsbnException(requestDTO.getIsbn());
        }

        Book book = bookMapper.toEntity(requestDTO);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponseDTO(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookMapper.toResponseDTO(book);
    }

    @Transactional(readOnly = true)
    public BookStockDTO getBookStock(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookMapper.toStockDTO(book);
    }

    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO requestDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // Verificar si el ISBN cambió y si ya existe
        if (!book.getIsbn().equals(requestDTO.getIsbn()) &&
                bookRepository.existsByIsbn(requestDTO.getIsbn())) {
            throw new DuplicateIsbnException(requestDTO.getIsbn());
        }

        book.setTitle(requestDTO.getTitle());
        book.setAuthor(requestDTO.getAuthor());
        book.setIsbn(requestDTO.getIsbn());
        book.setAvailableCopies(requestDTO.getAvailableCopies());

        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponseDTO(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public void decreaseStock(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (book.getAvailableCopies() <= 0) {
            throw new InsufficientStockException(id);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    @Transactional
    public void increaseStock(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}
