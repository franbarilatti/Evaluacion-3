package com.biblioteca.prestamos.client;

import com.biblioteca.prestamos.dto.UserStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios-service", url = "http://localhost:8082/api/usuarios")
public interface UserClient {
    @GetMapping("/{id}/status")
    UserStatusDTO getUserStatus(@PathVariable Long id);

    @GetMapping("/{id}/validate-active")
    void validateUserIsActive(@PathVariable Long id);
}
