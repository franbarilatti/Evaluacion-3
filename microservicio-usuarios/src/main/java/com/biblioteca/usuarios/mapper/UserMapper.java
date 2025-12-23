package com.biblioteca.usuarios.mapper;

import com.biblioteca.usuarios.dto.UserRequestDTO;
import com.biblioteca.usuarios.dto.UserResponseDTO;
import com.biblioteca.usuarios.dto.UserStatusDTO;
import com.biblioteca.usuarios.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setActive(true);
        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getActive()
        );
    }

    public UserStatusDTO toStatusDTO(User user) {
        return new UserStatusDTO(
                user.getId(),
                user.getFullName(),
                user.getActive()
        );
    }
}
