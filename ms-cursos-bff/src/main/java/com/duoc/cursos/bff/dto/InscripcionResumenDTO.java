package com.duoc.cursos.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InscripcionResumenDTO {

    private Long inscripcionId;
    private Long estudianteId;
    private String estudiante;
    private String correoEstudiante;
    private LocalDateTime fechaInscripcion;
    private List<DetalleInscripcionResumenDTO> cursos;
    private BigDecimal totalPagar;
    private String estado;

    public InscripcionResumenDTO() {
    }

    public InscripcionResumenDTO(Long inscripcionId,
                                 Long estudianteId,
                                 String estudiante,
                                 String correoEstudiante,
                                 LocalDateTime fechaInscripcion,
                                 List<DetalleInscripcionResumenDTO> cursos,
                                 BigDecimal totalPagar,
                                 String estado) {
        this.inscripcionId = inscripcionId;
        this.estudianteId = estudianteId;
        this.estudiante = estudiante;
        this.correoEstudiante = correoEstudiante;
        this.fechaInscripcion = fechaInscripcion;
        this.cursos = cursos;
        this.totalPagar = totalPagar;
        this.estado = estado;
    }

    public Long getInscripcionId() {
        return inscripcionId;
    }

    public void setInscripcionId(Long inscripcionId) {
        this.inscripcionId = inscripcionId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getCorreoEstudiante() {
        return correoEstudiante;
    }

    public void setCorreoEstudiante(String correoEstudiante) {
        this.correoEstudiante = correoEstudiante;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public List<DetalleInscripcionResumenDTO> getCursos() {
        return cursos;
    }

    public void setCursos(List<DetalleInscripcionResumenDTO> cursos) {
        this.cursos = cursos;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
