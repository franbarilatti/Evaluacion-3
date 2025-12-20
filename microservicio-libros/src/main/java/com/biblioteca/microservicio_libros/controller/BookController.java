package com.biblioteca.microservicio_libros.controller;

import com.biblioteca.microservicio_libros.dto.BookRequestDTO;
import com.biblioteca.microservicio_libros.dto.BookResponseDTO;
import com.biblioteca.microservicio_libros.dto.BookStockDTO;
import com.biblioteca.microservicio_libros.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "API de gestion de libros")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Crear un libro")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO dto){
        BookResponseDTO response = bookService.createBook(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los libros")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks(){
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un libro por ID")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id){
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/{id}/stock")
    @Operation(summary = "Consultar disponibilidad de un libro")
    public ResponseEntity<BookStockDTO> getBookStock(@PathVariable Long id){
        BookStockDTO stock = bookService.getBookStock(id);
        return ResponseEntity.ok(stock);
    }

    @PutMapping("/id")
    @Operation(summary = "Actualizar un libro existente")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDTO requestDTO){
        BookResponseDTO response = bookService.updateBook(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un Libro")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/decrese-stock")
    @Operation(summary = "Disminuir stock de un libro (uso interno)")
    public ResponseEntity<Void> decreaseStock(@PathVariable Long id){
        bookService.decreaseStock(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/increase-stock")
    @Operation(summary = "Aumentar stock de un libro (uso interno)")
    public ResponseEntity<Void> increaseStock(@PathVariable Long id){
        bookService.increaseStock(id);
        return ResponseEntity.ok().build();
    }

}
