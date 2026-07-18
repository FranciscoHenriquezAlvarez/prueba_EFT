package com.duoc.eventos.academicos.consumidor.exception;

import java.time.LocalDateTime;

// DTO que representa una respuesta estandar de error de la API.
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
