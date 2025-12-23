package com.biblioteca.microservicio_libros.controller;

import com.biblioteca.microservicio_libros.dto.BookRequestDTO;
import com.biblioteca.microservicio_libros.dto.BookResponseDTO;
import com.biblioteca.microservicio_libros.dto.BookStockDTO;
import com.biblioteca.microservicio_libros.exception.BookNotFoundException;
import com.biblioteca.microservicio_libros.exception.DuplicateIsbnException;
import com.biblioteca.microservicio_libros.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private ObjectMapper objectMapper;
    private BookRequestDTO requestDTO;
    private BookResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Configurar ObjectMapper manualmente con soporte para Java 8 Time
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    void whenCreateBook_thenReturnCreated() throws Exception {
        when(bookService.createBook(any(BookRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("El Principito"))
                .andExpect(jsonPath("$.author").value("Antoine de Saint-Exupéry"));

        verify(bookService, times(1)).createBook(any(BookRequestDTO.class));
    }

    @Test
    void whenCreateBook_withInvalidData_thenReturnBadRequest() throws Exception {
        BookRequestDTO invalidDTO = new BookRequestDTO("", "", "", -1);

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).createBook(any(BookRequestDTO.class));
    }

    @Test
    void whenCreateBook_withDuplicateIsbn_thenReturnConflict() throws Exception {
        when(bookService.createBook(any(BookRequestDTO.class)))
                .thenThrow(new DuplicateIsbnException("978-0156012195"));

        mockMvc.perform(post("/api/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe un libro con el ISBN: 978-0156012195"));
    }

    @Test
    void whenGetAllBooks_thenReturnBookList() throws Exception {
        BookResponseDTO book2 = new BookResponseDTO(
                2L, "1984", "George Orwell", "978-0451524935", 3
        );
        List<BookResponseDTO> books = Arrays.asList(responseDTO, book2);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("El Principito"))
                .andExpect(jsonPath("$[1].title").value("1984"));
    }

    @Test
    void whenGetBookById_thenReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("El Principito"));
    }

    @Test
    void whenGetBookById_withInvalidId_thenReturnNotFound() throws Exception {
        when(bookService.getBookById(99L))
                .thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(get("/api/libros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró el libro con id: 99"));
    }

    @Test
    void whenGetBookStock_thenReturnStock() throws Exception {
        BookStockDTO stockDTO = new BookStockDTO(1L, "El Principito", 5, true);
        when(bookService.getBookStock(1L)).thenReturn(stockDTO);

        mockMvc.perform(get("/api/libros/1/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(5))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void whenUpdateBook_thenReturnUpdatedBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("El Principito"));
    }

    @Test
    void whenDeleteBook_thenReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/libros/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void whenDecreaseStock_thenReturnOk() throws Exception {
        doNothing().when(bookService).decreaseStock(1L);

        mockMvc.perform(patch("/api/libros/1/decrease-stock"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).decreaseStock(1L);
    }

    @Test
    void whenIncreaseStock_thenReturnOk() throws Exception {
        doNothing().when(bookService).increaseStock(1L);

        mockMvc.perform(patch("/api/libros/1/increase-stock"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).increaseStock(1L);
    }
}