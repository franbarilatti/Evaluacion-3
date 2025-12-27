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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final UserClient userClient;
    private final BookClient bookClient;

    @Transactional
    public LoanResponseDTO createLoan(LoanRequestDTO requestDTO) {
        try {
            userClient.validateUserIsActive(requestDTO.getUserId());
        } catch (FeignException.NotFound e) {
            throw new ExternalServiceException("Usuarios", "Usuario con id " + requestDTO.getUserId() + " no encontrado");
        } catch (FeignException.BadRequest e) {
            throw new ExternalServiceException("Usuarios", "Usuario con id " + requestDTO.getUserId() + " no está activo");
        } catch (FeignException e) {
            throw new ExternalServiceException("Usuarios", "Error al validar usuario: " + e.getMessage());
        }

        try {
            BookStockDTO bookStock = bookClient.getBookStock(requestDTO.getBookId());
            if (!bookStock.isAvailable() || bookStock.getAvailableCopies() <= 0) {
                throw new ExternalServiceException("Libros", "Libro con id " + requestDTO.getBookId() + " no tiene ejemplares disponibles");
            }
        } catch (FeignException.NotFound e) {
            throw new ExternalServiceException("Libros", "Libro con id " + requestDTO.getBookId() + " no encontrado");
        } catch (FeignException e) {
            throw new ExternalServiceException("Libros", "Error al validar libro: " + e.getMessage());
        }
        try {
            bookClient.decreaseStock(requestDTO.getBookId());
        } catch (FeignException e) {
            throw new ExternalServiceException("Libros", "Error al actualizar stock: " + e.getMessage());
        }

        Loan loan = loanMapper.toEntity(requestDTO);
        Loan savedLoan = loanRepository.save(loan);

        log.info("Préstamo creado exitosamente: Usuario {} - Libro {}", requestDTO.getUserId(), requestDTO.getBookId());

        return loanMapper.toResponseDTO(savedLoan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoanResponseDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));
        return loanMapper.toResponseDTO(loan);
    }

    @Transactional
    public LoanResponseDTO returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(id));

        if (loan.getReturnDate() != null) {
            throw new LoanAlreadyReturnedException(id);
        }

        loan.setReturnDate(LocalDate.now());

        try {
            bookClient.increaseStock(loan.getBookId());
        } catch (FeignException e) {
            throw new ExternalServiceException("Libros", "Error al actualizar stock: " + e.getMessage());
        }

        Loan updatedLoan = loanRepository.save(loan);

        log.info("Préstamo devuelto exitosamente: ID {} - Libro {}", id, loan.getBookId());

        return loanMapper.toResponseDTO(updatedLoan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getActiveLoans() {
        return loanRepository.findByReturnDateIsNull()
                .stream()
                .map(loanMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId)
                .stream()
                .map(loanMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getLoansByBookId(Long bookId) {
        return loanRepository.findByBookId(bookId)
                .stream()
                .map(loanMapper::toResponseDTO)
                .toList();
    }


}
