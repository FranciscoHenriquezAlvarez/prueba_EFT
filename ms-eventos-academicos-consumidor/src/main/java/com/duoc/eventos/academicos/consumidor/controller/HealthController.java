package com.duoc.eventos.academicos.consumidor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
// Controlador REST que expone el estado basico del microservicio consumidor.
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "OK",
                "service", "ms-eventos-academicos-consumidor",
                "timestamp", LocalDateTime.now()
        );
    }
}
