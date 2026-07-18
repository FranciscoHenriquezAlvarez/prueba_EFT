package com.duoc.eventos.academicos.consumidor.dto;

import java.time.LocalDateTime;

public record EventoAcademicoErrorMensaje(
        String mensajeId,
        String tipoEvento,
        String colaOrigen,
        String payloadOriginal,
        String detalleError,
        LocalDateTime fechaError
) {
}
