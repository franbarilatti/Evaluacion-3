package com.biblioteca.usuarios.controller;

import com.biblioteca.usuarios.cotroller.UserController;
import com.biblioteca.usuarios.dto.UserRequestDTO;
import com.biblioteca.usuarios.dto.UserResponseDTO;
import com.biblioteca.usuarios.dto.UserStatusDTO;
import com.biblioteca.usuarios.exception.DuplicateEmailException;
import com.biblioteca.usuarios.exception.UserNotFoundException;
import com.biblioteca.usuarios.service.UserService;
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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    void whenCreateUser_thenReturnCreated() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    void whenCreateUser_withInvalidData_thenReturnBadRequest() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO("", "invalid-email");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    void whenCreateUser_withDuplicateEmail_thenReturnConflict() throws Exception {
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new DuplicateEmailException("juan.perez@example.com"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe un usuario con el email: juan.perez@example.com"));
    }

    @Test
    void whenGetAllUsers_thenReturnUserList() throws Exception {
        UserResponseDTO user2 = new UserResponseDTO(
                2L, "María García", "maria.garcia@example.com", true
        );
        List<UserResponseDTO> users = Arrays.asList(responseDTO, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].fullName").value("María García"));
    }

    @Test
    void whenGetUserById_thenReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"));
    }

    @Test
    void whenGetUserById_withInvalidId_thenReturnNotFound() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró el usuario con id: 99"));
    }

    @Test
    void whenGetUserStatus_thenReturnStatus() throws Exception {
        UserStatusDTO statusDTO = new UserStatusDTO(1L, "Juan Pérez", true);
        when(userService.getUserStatus(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/usuarios/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Juan Pérez"));
    }
    @Test
    void whenDeactivateUser_thenReturnOk() throws Exception {
        doNothing().when(userService).deactivateUser(1L);

        mockMvc.perform(patch("/api/usuarios/1/desactivar"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deactivateUser(1L);
    }

    @Test
    void whenActivateUser_thenReturnOk() throws Exception {
        doNothing().when(userService).activateUser(1L);

        mockMvc.perform(patch("/api/usuarios/1/activar"))
                .andExpect(status().isOk());

        verify(userService, times(1)).activateUser(1L);
    }

    @Test
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void whenValidateUserIsActive_thenReturnOk() throws Exception {
        doNothing().when(userService).validateUserIsActive(1L);

        mockMvc.perform(get("/api/usuarios/1/validate-active"))
                .andExpect(status().isOk());

        verify(userService, times(1)).validateUserIsActive(1L);
    }

}