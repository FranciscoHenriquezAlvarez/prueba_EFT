package com.duoc.cursos.bff.dto;

public class EventoAcademicoResponseDTO {

    private String mensaje;
    private String mensajeId;
    private String tipoEvento;
    private Long inscripcionId;
    private Long examenId;
    private Long intentoId;

    public EventoAcademicoResponseDTO() {
    }

    public EventoAcademicoResponseDTO(String mensaje,
                                      String mensajeId,
                                      String tipoEvento,
                                      Long inscripcionId,
                                      Long examenId,
                                      Long intentoId) {
        this.mensaje = mensaje;
        this.mensajeId = mensajeId;
        this.tipoEvento = tipoEvento;
        this.inscripcionId = inscripcionId;
        this.examenId = examenId;
        this.intentoId = intentoId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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
}
