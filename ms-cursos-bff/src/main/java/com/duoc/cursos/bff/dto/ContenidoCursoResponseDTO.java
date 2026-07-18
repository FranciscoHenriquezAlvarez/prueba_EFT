package com.duoc.cursos.bff.dto;

import java.time.LocalDateTime;

public class ContenidoCursoResponseDTO {

    private Long id;
    private Long cursoId;
    private String titulo;
    private String descripcion;
    private String nombreArchivo;
    private String tipoContenido;
    private String bucketS3;
    private String keyS3;
    private String rutaTemporalEfs;
    private LocalDateTime fechaCreacion;

    public ContenidoCursoResponseDTO() {
    }

    public ContenidoCursoResponseDTO(Long id,
                                     Long cursoId,
                                     String titulo,
                                     String descripcion,
                                     String nombreArchivo,
                                     String tipoContenido,
                                     String bucketS3,
                                     String keyS3,
                                     String rutaTemporalEfs,
                                     LocalDateTime fechaCreacion) {
        this.id = id;
        this.cursoId = cursoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreArchivo = nombreArchivo;
        this.tipoContenido = tipoContenido;
        this.bucketS3 = bucketS3;
        this.keyS3 = keyS3;
        this.rutaTemporalEfs = rutaTemporalEfs;
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public String getBucketS3() {
        return bucketS3;
    }

    public void setBucketS3(String bucketS3) {
        this.bucketS3 = bucketS3;
    }

    public String getKeyS3() {
        return keyS3;
    }

    public void setKeyS3(String keyS3) {
        this.keyS3 = keyS3;
    }

    public String getRutaTemporalEfs() {
        return rutaTemporalEfs;
    }

    public void setRutaTemporalEfs(String rutaTemporalEfs) {
        this.rutaTemporalEfs = rutaTemporalEfs;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
