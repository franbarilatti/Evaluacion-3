package com.biblioteca.prestamos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Préstamos - Biblioteca Digital")
                        .version("1.0")
                        .description("Microservicio para gestión de préstamos de libros. Permite registrar préstamos, devoluciones y consultar el historial.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("soporte@biblioteca.com")));
    }
}
