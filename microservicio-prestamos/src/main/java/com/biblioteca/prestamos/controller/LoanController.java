package com.biblioteca.prestamos.controller;

import com.biblioteca.prestamos.dto.LoanRequestDTO;
import com.biblioteca.prestamos.dto.LoanResponseDTO;
import com.biblioteca.prestamos.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
@Tag(name = "Préstamos", description = "API para gestión de préstamos de libros")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Crear un nuevo préstamo", description = "Registra un préstamo validando usuario activo y disponibilidad del libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario inactivo"),
            @ApiResponse(responseCode = "503", description = "Error en servicios externos")
    })
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody LoanRequestDTO requestDTO) {
        LoanResponseDTO response = loanService.createLoan(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los préstamos", description = "Lista todos los préstamos registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida correctamente")
    public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
        List<LoanResponseDTO> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener préstamo por ID", description = "Busca un préstamo específico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    public ResponseEntity<LoanResponseDTO> getLoanById(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @PostMapping("/{id}/devolver")
    @Operation(summary = "Registrar devolución de préstamo", description = "Marca un préstamo como devuelto y actualiza el stock del libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolución registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "El préstamo ya fue devuelto"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
            @ApiResponse(responseCode = "503", description = "Error al actualizar stock del libro")
    })
    public ResponseEntity<LoanResponseDTO> returnLoan(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.returnLoan(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener préstamos activos", description = "Lista todos los préstamos que aún no han sido devueltos")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos activos obtenida correctamente")
    public ResponseEntity<List<LoanResponseDTO>> getActiveLoans() {
        List<LoanResponseDTO> activeLoans = loanService.getActiveLoans();
        return ResponseEntity.ok(activeLoans);
    }

    @GetMapping("/usuario/{userId}")
    @Operation(summary = "Obtener préstamos por usuario", description = "Lista todos los préstamos asociados a un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos del usuario obtenida correctamente")
    public ResponseEntity<List<LoanResponseDTO>> getLoansByUserId(@PathVariable Long userId) {
        List<LoanResponseDTO> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/libro/{bookId}")
    @Operation(summary = "Obtener préstamos por libro", description = "Lista todos los préstamos asociados a un libro específico")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos del libro obtenida correctamente")
    public ResponseEntity<List<LoanResponseDTO>> getLoansByBookId(@PathVariable Long bookId) {
        List<LoanResponseDTO> loans = loanService.getLoansByBookId(bookId);
        return ResponseEntity.ok(loans);
    }
}
