package com.duoc.eventos.academicos.consumidor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
// DTO que resume el resultado del consumo manual de mensajes.
public record ManualConsumptionResponse(
        String estado,
        String modoActual,
        String colaOrigen,
        Integer mensajesSolicitados,
        Integer mensajesConsumidos,
        String tablaDestino,
        Boolean consumoAutomaticoActivo,
        Boolean consumoManualDisponible,
        String mensaje
) {
}
