package com.biblioteca.usuarios.service;

import com.biblioteca.usuarios.dto.UserRequestDTO;
import com.biblioteca.usuarios.dto.UserResponseDTO;
import com.biblioteca.usuarios.dto.UserStatusDTO;
import com.biblioteca.usuarios.exception.DuplicateEmailException;
import com.biblioteca.usuarios.exception.UserNotActiveException;
import com.biblioteca.usuarios.exception.UserNotFoundException;
import com.biblioteca.usuarios.mapper.UserMapper;
import com.biblioteca.usuarios.model.User;
import com.biblioteca.usuarios.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Juan Pérez");
        testUser.setEmail("juan.perez@example.com");
        testUser.setActive(true);

        requestDTO = new UserRequestDTO(
                "Juan Pérez",
                "juan.perez@example.com"
        );

        responseDTO = new UserResponseDTO(
                1L,
                "Juan Pérez",
                "juan.perez@example.com",
                true
        );
    }

    @Test
    void whenCreateUser_thenUserIsCreated() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("Juan Pérez");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenCreateUser_withDuplicateEmail_thenThrowException() {
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Ya existe un usuario con el email");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenGetAllUsers_thenReturnUserList() {
        User user2 = new User();
        user2.setId(2L);
        user2.setFullName("María García");
        user2.setEmail("maria.garcia@example.com");
        user2.setActive(true);

        UserResponseDTO responseDTO2 = new UserResponseDTO(
                2L, "María García", "maria.garcia@example.com", true
        );

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));
        when(userMapper.toResponseDTO(testUser)).thenReturn(responseDTO);
        when(userMapper.toResponseDTO(user2)).thenReturn(responseDTO2);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFullName()).isEqualTo("Juan Pérez");
        assertThat(result.get(1).getFullName()).isEqualTo("María García");
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(testUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("Juan Pérez");
    }

    @Test
    void whenGetUserById_withInvalidId_thenThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("No se encontró el usuario con id: 99");
    }

    @Test
    void whenGetUserStatus_thenReturnStatus() {
        UserStatusDTO statusDTO = new UserStatusDTO(1L, "Juan Pérez", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toStatusDTO(testUser)).thenReturn(statusDTO);

        UserStatusDTO result = userService.getUserStatus(1L);

        assertThat(result).isNotNull();
        assertThat(result.getActive()).isTrue();
    }

    @Test
    void whenUpdateUser_thenUserIsUpdated() {
        UserRequestDTO updateDTO = new UserRequestDTO(
                "Juan Pérez Actualizado",
                "juan.perez@example.com"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(responseDTO);

        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenDeactivateUser_thenUserIsDeactivated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenActivateUser_thenUserIsActivated() {
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.activateUser(1L);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteUser_withInvalidId_thenThrowException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void whenValidateUserIsActive_withActiveUser_thenNoException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.validateUserIsActive(1L);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void whenValidateUserIsActive_withInactiveUser_thenThrowException() {
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.validateUserIsActive(1L))
                .isInstanceOf(UserNotActiveException.class)
                .hasMessageContaining("no está activo");
    }
}
