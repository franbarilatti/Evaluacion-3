package com.biblioteca.usuarios.repository;

import com.biblioteca.usuarios.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFullName("Juan Pérez");
        testUser.setEmail("juan.perez@example.com");
        testUser.setActive(true);
    }

    @Test
    void whenSaveUser_thenUserIsSaved() {
        User savedUser = userRepository.save(testUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getFullName()).isEqualTo("Juan Pérez");
        assertThat(savedUser.getActive()).isTrue();
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("juan.perez@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Juan Pérez");
    }

    @Test
    void whenFindByEmail_withNonExistentEmail_thenReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("noexiste@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        userRepository.save(testUser);

        boolean exists = userRepository.existsByEmail("juan.perez@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmail_withNonExistentEmail_thenReturnFalse() {
        boolean exists = userRepository.existsByEmail("noexiste@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        userRepository.deleteById(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void whenFindAll_thenReturnUserList() {
        User user2 = new User();
        user2.setFullName("María García");
        user2.setEmail("maria.garcia@example.com");
        user2.setActive(true);

        userRepository.save(testUser);
        userRepository.save(user2);

        assertThat(userRepository.findAll()).hasSize(2);
    }
}
