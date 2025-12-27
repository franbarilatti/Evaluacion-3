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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException(requestDTO.getEmail());
        }

        User user = userMapper.toEntity(requestDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserStatusDTO getUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toStatusDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Verificar si el email cambió y si ya existe
        if (!user.getEmail().equals(requestDTO.getEmail()) &&
                userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateEmailException(requestDTO.getEmail());
        }

        user.setFullName(requestDTO.getFullName());
        user.setEmail(requestDTO.getEmail());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public void validateUserIsActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new UserNotActiveException(id);
        }
    }
}
