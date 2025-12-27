package com.biblioteca.prestamos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroservicioPrestamosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioPrestamosApplication.class, args);
	}

}
