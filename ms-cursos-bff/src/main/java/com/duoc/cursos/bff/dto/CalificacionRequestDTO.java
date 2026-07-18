package com.duoc.cursos.bff.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CalificacionRequestDTO {

    @NotNull(message = "El puntajeObtenido es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El puntaje no puede ser negativo")
    private BigDecimal puntajeObtenido;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "1.0", inclusive = true, message = "La nota debe ser mayor o igual a 1.0")
    @DecimalMax(value = "7.0", inclusive = true, message = "La nota debe ser menor o igual a 7.0")
    private BigDecimal nota;

    @NotBlank(message = "La observacion es obligatoria")
    private String observacion;

    public BigDecimal getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(BigDecimal puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
