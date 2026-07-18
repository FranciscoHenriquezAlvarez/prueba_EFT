package com.duoc.cursos.bff.dto;

import java.time.LocalDateTime;

public class EventoAcademicoMensaje {

    private String mensajeId;
    private String tipoEvento;
    private Long inscripcionId;
    private Long estudianteId;
    private Long cursoId;
    private Long examenId;
    private Long intentoId;
    private LocalDateTime fechaEvento;
    private String servicioOrigen;

    public EventoAcademicoMensaje() {
    }

    public EventoAcademicoMensaje(String mensajeId,
                                  String tipoEvento,
                                  Long inscripcionId,
                                  Long estudianteId,
                                  Long cursoId,
                                  Long examenId,
                                  Long intentoId,
                                  LocalDateTime fechaEvento,
                                  String servicioOrigen) {
        this.mensajeId = mensajeId;
        this.tipoEvento = tipoEvento;
        this.inscripcionId = inscripcionId;
        this.estudianteId = estudianteId;
        this.cursoId = cursoId;
        this.examenId = examenId;
        this.intentoId = intentoId;
        this.fechaEvento = fechaEvento;
        this.servicioOrigen = servicioOrigen;
    }

    public String getMensajeId() {
        return mensajeId;
    }

    public void setMensajeId(String mensajeId) {
        this.mensajeId = mensajeId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
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

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getExamenId() {
        return examenId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
    }

    public Long getIntentoId() {
        return intentoId;
    }

    public void setIntentoId(Long intentoId) {
        this.intentoId = intentoId;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getServicioOrigen() {
        return servicioOrigen;
    }

    public void setServicioOrigen(String servicioOrigen) {
        this.servicioOrigen = servicioOrigen;
    }
}
