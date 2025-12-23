package com.biblioteca.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {
    private Long id;
    private String fullName;
    private Boolean active;
}