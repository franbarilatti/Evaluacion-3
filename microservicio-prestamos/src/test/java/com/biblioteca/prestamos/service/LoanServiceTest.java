package com.biblioteca.prestamos.service;

import com.biblioteca.prestamos.client.BookClient;
import com.biblioteca.prestamos.client.UserClient;
import com.biblioteca.prestamos.dto.BookStockDTO;
import com.biblioteca.prestamos.dto.LoanRequestDTO;
import com.biblioteca.prestamos.dto.LoanResponseDTO;
import com.biblioteca.prestamos.exception.ExternalServiceException;
import com.biblioteca.prestamos.exception.LoanAlreadyReturnedException;
import com.biblioteca.prestamos.exception.LoanNotFoundException;
import com.biblioteca.prestamos.mapper.LoanMapper;
import com.biblioteca.prestamos.model.Loan;
import com.biblioteca.prestamos.repository.LoanRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private UserClient userClient;

    @Mock
    private BookClient bookClient;

    @InjectMocks
    private LoanService loanService;

    private LoanRequestDTO requestDTO;
    private Loan loan;
    private LoanResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new LoanRequestDTO(1L, 2L);

        loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setBookId(2L);
        loan.setLoanDate(LocalDate.now());

        responseDTO = new LoanResponseDTO(1L, 1L, 2L, LocalDate.now(), null);
    }

    @Test
    @DisplayName("Debe crear un préstamo exitosamente cuando usuario está activo y libro disponible")
    void createLoan_Success() {
        BookStockDTO stockDTO = new BookStockDTO(2L, "Harry Postre", 5, true);

        doNothing().when(userClient).validateUserIsActive(1L);
        when(bookClient.getBookStock(2L)).thenReturn(stockDTO);
        doNothing().when(bookClient).decreaseStock(2L);
        when(loanMapper.toEntity(requestDTO)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);

        LoanResponseDTO result = loanService.createLoan(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(2L, result.getBookId());

        verify(userClient).validateUserIsActive(1L);
        verify(bookClient).getBookStock(2L);
        verify(bookClient).decreaseStock(2L);
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe")
    void createLoan_UserNotFound() {
        FeignException.NotFound exception = mock(FeignException.NotFound.class);
        doThrow(exception).when(userClient).validateUserIsActive(1L);

        ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> loanService.createLoan(requestDTO)
        );

        assertTrue(thrown.getMessage().contains("Usuario con id 1 no encontrado"));
        verify(bookClient, never()).getBookStock(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no está activo")
    void createLoan_UserNotActive() {
        FeignException.BadRequest exception = mock(FeignException.BadRequest.class);
        doThrow(exception).when(userClient).validateUserIsActive(1L);

        ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> loanService.createLoan(requestDTO)
        );

        assertTrue(thrown.getMessage().contains("no está activo"));
        verify(bookClient, never()).getBookStock(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando libro no tiene stock disponible")
    void createLoan_NoStock() {
        BookStockDTO stockDTO = new BookStockDTO(2L, "Harry Postre", 0, false);

        doNothing().when(userClient).validateUserIsActive(1L);
        when(bookClient.getBookStock(2L)).thenReturn(stockDTO);

        ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> loanService.createLoan(requestDTO)
        );

        assertTrue(thrown.getMessage().contains("no tiene ejemplares disponibles"));
        verify(bookClient, never()).decreaseStock(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener todos los préstamos correctamente")
    void getAllLoans_Success() {
        List<Loan> loans = Arrays.asList(loan);
        when(loanRepository.findAll()).thenReturn(loans);
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);

        List<LoanResponseDTO> result = loanService.getAllLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository).findAll();
    }

    @Test
    @DisplayName("Debe obtener un préstamo por ID correctamente")
    void getLoanById_Success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);

        LoanResponseDTO result = loanService.getLoanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(loanRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando préstamo no existe")
    void getLoanById_NotFound() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById(99L));
        verify(loanRepository).findById(99L);
    }

    @Test
    @DisplayName("Debe devolver un préstamo exitosamente")
    void returnLoan_Success() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);
        doNothing().when(bookClient).increaseStock(2L);

        LoanResponseDTO result = loanService.returnLoan(1L);

        assertNotNull(result);
        assertNotNull(loan.getReturnDate());
        verify(bookClient).increaseStock(2L);
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar devolver préstamo ya devuelto")
    void returnLoan_AlreadyReturned() {
        loan.setReturnDate(LocalDate.now().minusDays(5));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnLoan(1L));
        verify(bookClient, never()).increaseStock(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener préstamos activos correctamente")
    void getActiveLoans_Success() {
        List<Loan> activeLoans = Arrays.asList(loan);
        when(loanRepository.findByReturnDateIsNull()).thenReturn(activeLoans);
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);

        List<LoanResponseDTO> result = loanService.getActiveLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository).findByReturnDateIsNull();
    }

    @Test
    @DisplayName("Debe obtener préstamos por usuario correctamente")
    void getLoansByUserId_Success() {
        List<Loan> userLoans = Arrays.asList(loan);
        when(loanRepository.findByUserId(1L)).thenReturn(userLoans);
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);

        List<LoanResponseDTO> result = loanService.getLoansByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Debe obtener préstamos por libro correctamente")
    void getLoansByBookId_Success() {
        List<Loan> bookLoans = Arrays.asList(loan);
        when(loanRepository.findByBookId(2L)).thenReturn(bookLoans);
        when(loanMapper.toResponseDTO(loan)).thenReturn(responseDTO);

        List<LoanResponseDTO> result = loanService.getLoansByBookId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(loanRepository).findByBookId(2L);
    }
}