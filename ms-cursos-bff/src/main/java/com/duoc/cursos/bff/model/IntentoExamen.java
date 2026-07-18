package com.duoc.cursos.bff.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "INTENTOS_EXAMEN")
public class IntentoExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "EXAMEN_ID", nullable = false)
    private Examen examen;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ESTUDIANTE_ID", nullable = false)
    private Estudiante estudiante;

    @Lob
    @Column(name = "RESPUESTAS_JSON", nullable = false)
    private String respuestasJson;

    @Column(name = "FECHA_REALIZACION", nullable = false)
    private LocalDateTime fechaRealizacion;

    @Column(name = "ESTADO", nullable = false, length = 40)
    private String estado;

    @Column(name = "PUNTAJE_OBTENIDO", precision = 10, scale = 2)
    private BigDecimal puntajeObtenido;

    @Column(name = "NOTA", precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(name = "OBSERVACION_PROFESOR", length = 500)
    private String observacionProfesor;

    @Column(name = "FECHA_CALIFICACION")
    private LocalDateTime fechaCalificacion;

    @PrePersist
    void onCreate() {
        if (fechaRealizacion == null) {
            fechaRealizacion = LocalDateTime.now();
        }
        if (estado == null || estado.isBlank()) {
            estado = "ENVIADO";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
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
