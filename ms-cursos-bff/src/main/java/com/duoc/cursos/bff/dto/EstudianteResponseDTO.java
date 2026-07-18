package com.duoc.cursos.bff.dto;

public class EstudianteResponseDTO {

    private Long id;
    private String nombre;
    private String correo;
    private String identificadorIdaas;
    private Boolean activo;

    public EstudianteResponseDTO() {
    }

    public EstudianteResponseDTO(Long id,
                                 String nombre,
                                 String correo,
                                 String identificadorIdaas,
                                 Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.identificadorIdaas = identificadorIdaas;
        this.activo = activo;
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
