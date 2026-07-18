package com.duoc.cursos.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExamenResponseDTO {

    private Long id;
    private Long cursoId;
    private String titulo;
    private String descripcion;
    private String preguntasJson;
    private BigDecimal puntajeMaximo;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public ExamenResponseDTO() {
    }

    public ExamenResponseDTO(Long id,
                             Long cursoId,
                             String titulo,
                             String descripcion,
                             String preguntasJson,
                             BigDecimal puntajeMaximo,
                             Boolean activo,
                             LocalDateTime fechaCreacion) {
        this.id = id;
        this.cursoId = cursoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.preguntasJson = preguntasJson;
        this.puntajeMaximo = puntajeMaximo;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
