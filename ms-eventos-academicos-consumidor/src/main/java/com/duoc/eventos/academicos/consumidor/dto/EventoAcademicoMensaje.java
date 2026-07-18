package com.duoc.eventos.academicos.consumidor.dto;

import java.time.LocalDateTime;

public record EventoAcademicoMensaje(
        String mensajeId,
        String tipoEvento,
        Long inscripcionId,
        Long estudianteId,
        Long cursoId,
        Long examenId,
        Long intentoId,
        LocalDateTime fechaEvento,
        String servicioOrigen
) {
}
