package com.biblioteca.prestamos.client;

import com.biblioteca.prestamos.dto.BookStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "libros-service", url = "http://localhost:8081/api/libros")
public interface BookClient {
    @GetMapping("/{id}/stock")
    BookStockDTO getBookStock(@PathVariable Long id);

    @PatchMapping("/{id}/decrease-stock")
    void decreaseStock(@PathVariable Long id);

    @PatchMapping("/{id}/increase-stock")
    void increaseStock(@PathVariable Long id);
}
