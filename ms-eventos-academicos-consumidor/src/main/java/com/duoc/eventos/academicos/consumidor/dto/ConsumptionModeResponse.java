package com.duoc.eventos.academicos.consumidor.dto;

// DTO que informa el modo actual de consumo del listener RabbitMQ.
public record ConsumptionModeResponse(
        String modo,
        boolean consumoAutomaticoActivo,
        boolean consumoManualDisponible,
        String listenerId,
        String colaPrincipal,
        String mensaje
) {
}
