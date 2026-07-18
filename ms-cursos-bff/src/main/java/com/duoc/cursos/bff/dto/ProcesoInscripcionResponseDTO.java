package com.duoc.cursos.bff.dto;

public class ProcesoInscripcionResponseDTO {

    private String mensaje;
    private InscripcionResumenDTO inscripcion;
    private ArchivoResumenResponseDTO archivoResumen;
    private ArchivoResumenResponseDTO archivoS3;
    private EventoAcademicoResponseDTO eventoAcademico;

    public ProcesoInscripcionResponseDTO() {
    }

    public ProcesoInscripcionResponseDTO(String mensaje,
                                         InscripcionResumenDTO inscripcion,
                                         ArchivoResumenResponseDTO archivoResumen,
                                         ArchivoResumenResponseDTO archivoS3,
                                         EventoAcademicoResponseDTO eventoAcademico) {
        this.mensaje = mensaje;
        this.inscripcion = inscripcion;
        this.archivoResumen = archivoResumen;
        this.archivoS3 = archivoS3;
        this.eventoAcademico = eventoAcademico;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public InscripcionResumenDTO getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(InscripcionResumenDTO inscripcion) {
        this.inscripcion = inscripcion;
    }

    public ArchivoResumenResponseDTO getArchivoResumen() {
        return archivoResumen;
    }

    public void setArchivoResumen(ArchivoResumenResponseDTO archivoResumen) {
        this.archivoResumen = archivoResumen;
    }

    public ArchivoResumenResponseDTO getArchivoS3() {
        return archivoS3;
    }

    public void setArchivoS3(ArchivoResumenResponseDTO archivoS3) {
        this.archivoS3 = archivoS3;
    }

    public EventoAcademicoResponseDTO getEventoAcademico() {
        return eventoAcademico;
    }

    public void setEventoAcademico(EventoAcademicoResponseDTO eventoAcademico) {
        this.eventoAcademico = eventoAcademico;
    }
}
