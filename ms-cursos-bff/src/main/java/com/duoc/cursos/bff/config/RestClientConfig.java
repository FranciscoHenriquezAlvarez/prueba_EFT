package com.duoc.cursos.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
// Configuracion del RestClient usado para comunicarse con el consumidor academico.
public class RestClientConfig {

    @Bean
    // Expone un builder reutilizable para las llamadas entre microservicios.
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
