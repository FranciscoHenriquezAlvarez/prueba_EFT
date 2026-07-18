package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.service.BffOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bff")
public class BffOrchestrationController {

    private final BffOrchestrationService bffOrchestrationService;

    public BffOrchestrationController(BffOrchestrationService bffOrchestrationService) {
        this.bffOrchestrationService = bffOrchestrationService;
    }

    @PostMapping("/eventos/consumir")
    public ResponseEntity<Map<String, Object>> consumirEventos(@RequestParam(defaultValue = "1") int cantidad,
                                                               @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(bffOrchestrationService.solicitarConsumoManual(cantidad, authorization));
    }
}
