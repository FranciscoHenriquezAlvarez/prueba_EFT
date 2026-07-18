package com.duoc.cursos.bff.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ExamenRequestDTO {

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotBlank(message = "Las preguntasJson son obligatorias")
    private String preguntasJson;

    @NotNull(message = "El puntaje maximo es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El puntaje maximo debe ser mayor que cero")
    private BigDecimal puntajeMaximo;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPreguntasJson() {
        return preguntasJson;
    }

    public void setPreguntasJson(String preguntasJson) {
        this.preguntasJson = preguntasJson;
    }

    public BigDecimal getPuntajeMaximo() {
        return puntajeMaximo;
    }

    public void setPuntajeMaximo(BigDecimal puntajeMaximo) {
        this.puntajeMaximo = puntajeMaximo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
