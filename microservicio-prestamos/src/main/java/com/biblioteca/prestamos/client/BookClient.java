package com.biblioteca.prestamos.client;

import com.biblioteca.prestamos.dto.BookStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "libros-service",
        url = "${libros.service.url}"
)
public interface BookClient {

    @GetMapping("/api/libros/{id}/stock")
    BookStockDTO getBookStock(@PathVariable Long id);

    @PatchMapping("/api/libros/{id}/decrease-stock")
    void decreaseStock(@PathVariable Long id);

    @PatchMapping("/api/libros/{id}/increase-stock")
    void increaseStock(@PathVariable Long id);
}
