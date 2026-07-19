package com.duoc.eventos.academicos.consumidor.controller;

import com.duoc.eventos.academicos.consumidor.dto.ConsumptionModeResponse;
import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoMqResponse;
import com.duoc.eventos.academicos.consumidor.dto.ManualConsumptionResponse;
import com.duoc.eventos.academicos.consumidor.service.EventoAcademicoConsumerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/mq")
// Controlador REST que expone evidencias y operaciones del consumo RabbitMQ.
public class MqEvidenceController {

    private final EventoAcademicoConsumerService eventoAcademicoConsumerService;

    public MqEvidenceController(EventoAcademicoConsumerService eventoAcademicoConsumerService) {
        this.eventoAcademicoConsumerService = eventoAcademicoConsumerService;
    }

    @GetMapping("/procesados")
    public List<EventoAcademicoMqResponse> listarProcesados() {
        return eventoAcademicoConsumerService.listarProcesados();
    }

    @GetMapping("/errores")
    public List<EventoAcademicoMqResponse> listarErrores() {
        return eventoAcademicoConsumerService.listarErrores();
    }

    @GetMapping("/modo-consumo")
    public ConsumptionModeResponse obtenerModoConsumo() {
        return eventoAcademicoConsumerService.obtenerModoConsumo();
    }

    @PutMapping("/modo-consumo")
    // Alterna entre listener automatico y consumo manual controlado.
    public ConsumptionModeResponse cambiarModoConsumo(@RequestParam boolean automatico) {
        return eventoAcademicoConsumerService.cambiarModoConsumo(automatico);
    }

    @PostMapping("/consumir")
    // Ejecuta una lectura bajo demanda cuando el listener esta detenido.
    public ResponseEntity<ManualConsumptionResponse> consumirManualmente(
            @RequestParam(defaultValue = "1") int cantidad) {
        ManualConsumptionResponse response = eventoAcademicoConsumerService.consumirManualmente(cantidad);
        HttpStatus status = "NO_DISPONIBLE".equals(response.estado()) ? HttpStatus.CONFLICT : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
