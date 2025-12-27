package com.biblioteca.prestamos.client;

import com.biblioteca.prestamos.dto.UserStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "usuarios-service",
        url = "${usuarios.service.url}"
)
public interface UserClient {

    @GetMapping("/api/usuarios/{id}/status")
    UserStatusDTO getUserStatus(@PathVariable Long id);

    @GetMapping("/api/usuarios/{id}/validate-active")
    void validateUserIsActive(@PathVariable Long id);
}
