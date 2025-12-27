package com.biblioteca.prestamos.cotroller;

import com.biblioteca.prestamos.controller.LoanController;
import com.biblioteca.prestamos.dto.LoanRequestDTO;
import com.biblioteca.prestamos.dto.LoanResponseDTO;
import com.biblioteca.prestamos.exception.LoanAlreadyReturnedException;
import com.biblioteca.prestamos.exception.LoanNotFoundException;
import com.biblioteca.prestamos.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanService loanService;

    private LoanRequestDTO requestDTO;
    private LoanResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new LoanRequestDTO(1L, 2L);
        responseDTO = new LoanResponseDTO(1L, 1L, 2L, LocalDate.now(), null);
    }

    @Test
    @DisplayName("POST /api/prestamos debe crear un préstamo y retornar 201")
    void createLoan_Success() throws Exception {
        when(loanService.createLoan(any(LoanRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(2));
    }

    @Test
    @DisplayName("POST /api/prestamos debe retornar 400 cuando faltan campos obligatorios")
    void createLoan_ValidationError() throws Exception {
        LoanRequestDTO invalidRequest = new LoanRequestDTO(null, null);

        mockMvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/prestamos debe retornar lista de préstamos")
    void getAllLoans_Success() throws Exception {
        List<LoanResponseDTO> loans = Arrays.asList(responseDTO);
        when(loanService.getAllLoans()).thenReturn(loans);

        mockMvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/prestamos/{id} debe retornar un préstamo específico")
    void getLoanById_Success() throws Exception {
        when(loanService.getLoanById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/prestamos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(2));
    }

    @Test
    @DisplayName("GET /api/prestamos/{id} debe retornar 404 cuando no existe")
    void getLoanById_NotFound() throws Exception {
        when(loanService.getLoanById(99L)).thenThrow(new LoanNotFoundException(99L));

        mockMvc.perform(get("/api/prestamos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/prestamos/{id}/devolver debe registrar devolución exitosamente")
    void returnLoan_Success() throws Exception {
        LoanResponseDTO returnedLoan = new LoanResponseDTO(1L, 1L, 2L, LocalDate.now(), LocalDate.now());
        when(loanService.returnLoan(1L)).thenReturn(returnedLoan);

        mockMvc.perform(post("/api/prestamos/1/devolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnDate").exists());
    }

    @Test
    @DisplayName("POST /api/prestamos/{id}/devolver debe retornar 400 si ya fue devuelto")
    void returnLoan_AlreadyReturned() throws Exception {
        when(loanService.returnLoan(1L)).thenThrow(new LoanAlreadyReturnedException(1L));

        mockMvc.perform(post("/api/prestamos/1/devolver"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/prestamos/activos debe retornar préstamos activos")
    void getActiveLoans_Success() throws Exception {
        List<LoanResponseDTO> activeLoans = Arrays.asList(responseDTO);
        when(loanService.getActiveLoans()).thenReturn(activeLoans);

        mockMvc.perform(get("/api/prestamos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].returnDate").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/prestamos/usuario/{userId} debe retornar préstamos del usuario")
    void getLoansByUserId_Success() throws Exception {
        List<LoanResponseDTO> userLoans = Arrays.asList(responseDTO);
        when(loanService.getLoansByUserId(1L)).thenReturn(userLoans);

        mockMvc.perform(get("/api/prestamos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/prestamos/libro/{bookId} debe retornar préstamos del libro")
    void getLoansByBookId_Success() throws Exception {
        List<LoanResponseDTO> bookLoans = Arrays.asList(responseDTO);
        when(loanService.getLoansByBookId(2L)).thenReturn(bookLoans);

        mockMvc.perform(get("/api/prestamos/libro/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(2));
    }
}