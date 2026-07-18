package com.duoc.cursos.bff.exception;

// Se usa cuando una inscripcion solicitada no existe en la base de datos.
public class InscripcionNoEncontradaException extends RuntimeException {

    public InscripcionNoEncontradaException(Long inscripcionId) {
        super("Inscripcion no encontrada: " + inscripcionId);
    }
}
