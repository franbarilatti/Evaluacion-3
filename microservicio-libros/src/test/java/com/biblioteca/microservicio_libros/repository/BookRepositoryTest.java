package com.biblioteca.microservicio_libros.repository;

import com.biblioteca.microservicio_libros.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setTitle("El Principito");
        testBook.setAuthor("Antoine de Saint-Exupéry");
        testBook.setIsbn("978-0156012195");
        testBook.setAvailableCopies(5);
    }

    @Test
    void whenSaveBook_thenBookIsSaved() {
        Book savedBook = bookRepository.save(testBook);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("El Principito");
    }

    @Test
    void whenFindByIsbn_thenReturnBook() {
        bookRepository.save(testBook);

        Optional<Book> found = bookRepository.findByIsbn("978-0156012195");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("El Principito");
    }

    @Test
    void whenFindByIsbn_withNonExistentIsbn_thenReturnEmpty() {
        Optional<Book> found = bookRepository.findByIsbn("000-0000000000");

        assertThat(found).isEmpty();
    }

    @Test
    void whenExistsByIsbn_thenReturnTrue() {
        bookRepository.save(testBook);

        boolean exists = bookRepository.existsByIsbn("978-0156012195");

        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByIsbn_withNonExistentIsbn_thenReturnFalse() {
        boolean exists = bookRepository.existsByIsbn("000-0000000000");

        assertThat(exists).isFalse();
    }

    @Test
    void whenDeleteBook_thenBookIsDeleted() {
        Book savedBook = bookRepository.save(testBook);
        Long bookId = savedBook.getId();

        bookRepository.deleteById(bookId);

        Optional<Book> deletedBook = bookRepository.findById(bookId);
        assertThat(deletedBook).isEmpty();
    }

    @Test
    void whenFindAll_thenReturnBookList() {
        Book book1 = new Book();
        book1.setTitle("1984");
        book1.setAuthor("George Orwell");
        book1.setIsbn("978-0451524935");
        book1.setAvailableCopies(3);

        bookRepository.save(testBook);
        bookRepository.save(book1);

        assertThat(bookRepository.findAll()).hasSize(2);
    }
}
