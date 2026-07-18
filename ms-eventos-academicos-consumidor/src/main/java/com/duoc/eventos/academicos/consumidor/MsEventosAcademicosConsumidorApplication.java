package com.duoc.eventos.academicos.consumidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Clase principal que inicia el microservicio consumidor de eventos academicos.
public class MsEventosAcademicosConsumidorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsEventosAcademicosConsumidorApplication.class, args);
    }
}
