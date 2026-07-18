package com.duoc.cursos.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CursoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal costo;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public CursoResponseDTO() {
    }

    public CursoResponseDTO(Long id,
                            String nombre,
                            String descripcion,
                            BigDecimal costo,
                            Boolean activo,
                            LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
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
