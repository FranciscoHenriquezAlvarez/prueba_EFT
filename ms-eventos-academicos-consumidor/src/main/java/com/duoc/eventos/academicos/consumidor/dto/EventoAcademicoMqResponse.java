package com.duoc.eventos.academicos.consumidor.dto;

import java.time.LocalDateTime;

public record EventoAcademicoMqResponse(
        Long id,
        String mensajeId,
        String tipoEvento,
        Long inscripcionId,
        Long estudianteId,
        Long cursoId,
        Long examenId,
        Long intentoId,
        LocalDateTime fechaEvento,
        String servicioOrigen,
        String payloadJson,
        String colaOrigen,
        String estadoProcesamiento,
        String detalleError,
        LocalDateTime fechaProcesamiento
) {
}
