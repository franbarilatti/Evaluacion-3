package com.biblioteca.microservicio_libros.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Libros - API")
                        .version("1.0")
                        .description("API REST para la gestion de libros en el sistema de una biblioteca digital")
                        .contact(new Contact()
                                .name("Franco Barilatti")
                                .email("francoagustinbarilatti@hotmail.com")));
    }

}
