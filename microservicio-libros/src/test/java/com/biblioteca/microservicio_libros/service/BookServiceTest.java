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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookRequestDTO requestDTO;
    private BookResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("El Principito");
        testBook.setAuthor("Antoine de Saint-Exupéry");
        testBook.setIsbn("978-0156012195");
        testBook.setAvailableCopies(5);

        requestDTO = new BookRequestDTO(
                "El Principito",
                "Antoine de Saint-Exupéry",
                "978-0156012195",
                5
        );

        responseDTO = new BookResponseDTO(
                1L,
                "El Principito",
                "Antoine de Saint-Exupéry",
                "978-0156012195",
                5
        );
    }

    @Test
    void whenCreateBook_thenBookIsCreated() {
        when(bookRepository.existsByIsbn(requestDTO.getIsbn())).thenReturn(false);
        when(bookMapper.toEntity(requestDTO)).thenReturn(testBook);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);

        BookResponseDTO result = bookService.createBook(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("El Principito");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void whenCreateBook_withDuplicateIsbn_thenThrowException() {
        when(bookRepository.existsByIsbn(requestDTO.getIsbn())).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(requestDTO))
                .isInstanceOf(DuplicateIsbnException.class)
                .hasMessageContaining("Ya existe un libro con el ISBN");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void whenGetAllBooks_thenReturnBookList() {
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("1984");
        book2.setAuthor("George Orwell");
        book2.setIsbn("978-0451524935");
        book2.setAvailableCopies(3);

        BookResponseDTO responseDTO2 = new BookResponseDTO(
                2L, "1984", "George Orwell", "978-0451524935", 3
        );

        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook, book2));
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);
        when(bookMapper.toResponseDTO(book2)).thenReturn(responseDTO2);

        List<BookResponseDTO> result = bookService.getAllBooks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("El Principito");
        assertThat(result.get(1).getTitle()).isEqualTo("1984");
    }

    @Test
    void whenGetBookById_thenReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toResponseDTO(testBook)).thenReturn(responseDTO);

        BookResponseDTO result = bookService.getBookById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("El Principito");
    }

    @Test
    void whenGetBookById_withInvalidId_thenThrowException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("No se encontró el libro con id: 99");
    }

    @Test
    void whenGetBookStock_thenReturnStock() {
        BookStockDTO stockDTO = new BookStockDTO(1L, "El Principito", 5, true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toStockDTO(testBook)).thenReturn(stockDTO);

        BookStockDTO result = bookService.getBookStock(1L);

        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getAvailableCopies()).isEqualTo(5);
    }

    @Test
    void whenUpdateBook_thenBookIsUpdated() {
        BookRequestDTO updateDTO = new BookRequestDTO(
                "El Principito (Edición Especial)",
                "Antoine de Saint-Exupéry",
                "978-0156012195",
                10
        );

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(testBook));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(testBook);

        when(bookMapper.toResponseDTO(testBook))
                .thenReturn(responseDTO);

        BookResponseDTO result = bookService.updateBook(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void whenUpdateBook_withDuplicateIsbn_thenThrowException() {
        BookRequestDTO updateDTO = new BookRequestDTO(
                "El Principito",
                "Antoine de Saint-Exupéry",
                "978-0156012195",
                10
        );

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(testBook));

        when(bookRepository.existsByIsbn(updateDTO.getIsbn()))
                .thenReturn(true);

        assertThatThrownBy(() -> bookService.updateBook(1L, updateDTO))
                .isInstanceOf(DuplicateIsbnException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void whenDeleteBook_thenBookIsDeleted() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteBook_withInvalidId_thenThrowException() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void whenDecreaseStock_thenStockIsDecreased() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        bookService.decreaseStock(1L);

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void whenDecreaseStock_withZeroStock_thenThrowException() {
        testBook.setAvailableCopies(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        assertThatThrownBy(() -> bookService.decreaseStock(1L))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("no tiene ejemplares disponibles");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void whenIncreaseStock_thenStockIsIncreased() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        bookService.increaseStock(1L);

        verify(bookRepository, times(1)).save(any(Book.class));
    }
}
