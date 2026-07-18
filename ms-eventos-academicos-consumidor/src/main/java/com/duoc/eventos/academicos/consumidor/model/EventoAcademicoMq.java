package com.duoc.eventos.academicos.consumidor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTOS_ACADEMICOS_MQ")
public class EventoAcademicoMq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MENSAJE_ID", length = 120, unique = true)
    private String mensajeId;

    @Column(name = "TIPO_EVENTO", nullable = false, length = 80)
    private String tipoEvento;

    @Column(name = "INSCRIPCION_ID")
    private Long inscripcionId;

    @Column(name = "ESTUDIANTE_ID")
    private Long estudianteId;

    @Column(name = "CURSO_ID")
    private Long cursoId;

    @Column(name = "EXAMEN_ID")
    private Long examenId;

    @Column(name = "INTENTO_ID")
    private Long intentoId;

    @Column(name = "FECHA_EVENTO")
    private LocalDateTime fechaEvento;

    @Column(name = "SERVICIO_ORIGEN", length = 120)
    private String servicioOrigen;

    @Lob
    @Column(name = "PAYLOAD_JSON")
    private String payloadJson;

    @Column(name = "COLA_ORIGEN", length = 120)
    private String colaOrigen;

    @Column(name = "ESTADO_PROCESAMIENTO", nullable = false, length = 40)
    private String estadoProcesamiento;

    @Lob
    @Column(name = "DETALLE_ERROR")
    private String detalleError;

    @Column(name = "FECHA_PROCESAMIENTO", nullable = false)
    private LocalDateTime fechaProcesamiento;

    @PrePersist
    void onCreate() {
        if (fechaProcesamiento == null) {
            fechaProcesamiento = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getColaOrigen() {
        return colaOrigen;
    }

    public void setColaOrigen(String colaOrigen) {
        this.colaOrigen = colaOrigen;
    }

    public String getEstadoProcesamiento() {
        return estadoProcesamiento;
    }

    public void setEstadoProcesamiento(String estadoProcesamiento) {
        this.estadoProcesamiento = estadoProcesamiento;
    }

    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
}
