package com.duoc.cursos.bff.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IntentoExamenResponseDTO {

    private Long id;
    private Long examenId;
    private Long cursoId;
    private Long estudianteId;
    private String nombreEstudiante;
    private String respuestasJson;
    private LocalDateTime fechaRealizacion;
    private String estado;
    private BigDecimal puntajeObtenido;
    private BigDecimal nota;
    private String observacionProfesor;
    private LocalDateTime fechaCalificacion;

    public IntentoExamenResponseDTO() {
    }

    public IntentoExamenResponseDTO(Long id,
                                    Long examenId,
                                    Long cursoId,
                                    Long estudianteId,
                                    String nombreEstudiante,
                                    String respuestasJson,
                                    LocalDateTime fechaRealizacion,
                                    String estado,
                                    BigDecimal puntajeObtenido,
                                    BigDecimal nota,
                                    String observacionProfesor,
                                    LocalDateTime fechaCalificacion) {
        this.id = id;
        this.examenId = examenId;
        this.cursoId = cursoId;
        this.estudianteId = estudianteId;
        this.nombreEstudiante = nombreEstudiante;
        this.respuestasJson = respuestasJson;
        this.fechaRealizacion = fechaRealizacion;
        this.estado = estado;
        this.puntajeObtenido = puntajeObtenido;
        this.nota = nota;
        this.observacionProfesor = observacionProfesor;
        this.fechaCalificacion = fechaCalificacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamenId() {
        return examenId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getRespuestasJson() {
        return respuestasJson;
    }

    public void setRespuestasJson(String respuestasJson) {
        this.respuestasJson = respuestasJson;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

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

    public String getObservacionProfesor() {
        return observacionProfesor;
    }

    public void setObservacionProfesor(String observacionProfesor) {
        this.observacionProfesor = observacionProfesor;
    }

    public LocalDateTime getFechaCalificacion() {
        return fechaCalificacion;
    }

    public void setFechaCalificacion(LocalDateTime fechaCalificacion) {
        this.fechaCalificacion = fechaCalificacion;
    }
}
