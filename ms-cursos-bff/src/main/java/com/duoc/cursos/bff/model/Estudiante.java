package com.duoc.cursos.bff.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ESTUDIANTES")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "CORREO", nullable = false, unique = true, length = 180)
    private String correo;

    @Column(name = "IDENTIFICADOR_IDAAS", nullable = false, unique = true, length = 200)
    private String identificadorIdaas;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = Boolean.TRUE;

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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getIdentificadorIdaas() {
        return identificadorIdaas;
    }

    public void setIdentificadorIdaas(String identificadorIdaas) {
        this.identificadorIdaas = identificadorIdaas;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
