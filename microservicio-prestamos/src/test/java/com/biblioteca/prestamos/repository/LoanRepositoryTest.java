package com.biblioteca.prestamos.repository;

import com.biblioteca.prestamos.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    private Loan loan1;
    private Loan loan2;
    private Loan loan3;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();

        loan1 = new Loan();
        loan1.setUserId(1L);
        loan1.setBookId(10L);
        loan1.setLoanDate(LocalDate.now().minusDays(5));
        loan1.setReturnDate(null);

        loan2 = new Loan();
        loan2.setUserId(1L);
        loan2.setBookId(20L);
        loan2.setLoanDate(LocalDate.now().minusDays(10));
        loan2.setReturnDate(LocalDate.now().minusDays(3));

        loan3 = new Loan();
        loan3.setUserId(2L);
        loan3.setBookId(10L);
        loan3.setLoanDate(LocalDate.now().minusDays(2));
        loan3.setReturnDate(null);

        loanRepository.save(loan1);
        loanRepository.save(loan2);
        loanRepository.save(loan3);
    }

    @Test
    @DisplayName("Debe guardar un préstamo correctamente")
    void save_Success() {
        Loan newLoan = new Loan();
        newLoan.setUserId(3L);
        newLoan.setBookId(30L);
        newLoan.setLoanDate(LocalDate.now());

        Loan savedLoan = loanRepository.save(newLoan);

        assertNotNull(savedLoan.getId());
        assertEquals(3L, savedLoan.getUserId());
        assertEquals(30L, savedLoan.getBookId());
        assertNotNull(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Debe encontrar un préstamo por ID")
    void findById_Success() {
        Optional<Loan> found = loanRepository.findById(loan1.getId());

        assertTrue(found.isPresent());
        assertEquals(loan1.getUserId(), found.get().getUserId());
        assertEquals(loan1.getBookId(), found.get().getBookId());
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando préstamo no existe")
    void findById_NotFound() {
        Optional<Loan> found = loanRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar todos los préstamos")
    void findAll_Success() {
        List<Loan> loans = loanRepository.findAll();

        assertEquals(3, loans.size());
    }

    @Test
    @DisplayName("Debe encontrar préstamos activos (sin fecha de devolución)")
    void findByReturnDateIsNull_Success() {
        List<Loan> activeLoans = loanRepository.findByReturnDateIsNull();

        assertEquals(2, activeLoans.size());
        assertTrue(activeLoans.stream().allMatch(loan -> loan.getReturnDate() == null));
    }

    @Test
    @DisplayName("Debe encontrar préstamos por userId")
    void findByUserId_Success() {
        List<Loan> userLoans = loanRepository.findByUserId(1L);

        assertEquals(2, userLoans.size());
        assertTrue(userLoans.stream().allMatch(loan -> loan.getUserId().equals(1L)));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando usuario no tiene préstamos")
    void findByUserId_Empty() {
        List<Loan> userLoans = loanRepository.findByUserId(999L);

        assertTrue(userLoans.isEmpty());
    }

    @Test
    @DisplayName("Debe encontrar préstamos por bookId")
    void findByBookId_Success() {
        List<Loan> bookLoans = loanRepository.findByBookId(10L);

        assertEquals(2, bookLoans.size());
        assertTrue(bookLoans.stream().allMatch(loan -> loan.getBookId().equals(10L)));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando libro no tiene préstamos")
    void findByBookId_Empty() {
        List<Loan> bookLoans = loanRepository.findByBookId(999L);

        assertTrue(bookLoans.isEmpty());
    }

    @Test
    @DisplayName("Debe actualizar fecha de devolución correctamente")
    void updateReturnDate_Success() {
        Loan loanToReturn = loanRepository.findById(loan1.getId()).get();
        LocalDate returnDate = LocalDate.now();

        loanToReturn.setReturnDate(returnDate);
        Loan updated = loanRepository.save(loanToReturn);

        assertNotNull(updated.getReturnDate());
        assertEquals(returnDate, updated.getReturnDate());
    }

    @Test
    @DisplayName("Debe eliminar un préstamo correctamente")
    void delete_Success() {
        Long loanId = loan1.getId();

        loanRepository.deleteById(loanId);

        Optional<Loan> deleted = loanRepository.findById(loanId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Debe contar préstamos correctamente")
    void count_Success() {
        long count = loanRepository.count();

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Debe verificar si existe un préstamo por ID")
    void existsById_Success() {
        boolean exists = loanRepository.existsById(loan1.getId());
        boolean notExists = loanRepository.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }
}
