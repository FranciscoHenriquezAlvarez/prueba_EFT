package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.ArchivoResumenResponseDTO;
import com.duoc.cursos.bff.dto.EventoAcademicoResponseDTO;
import com.duoc.cursos.bff.dto.InscripcionRequestDTO;
import com.duoc.cursos.bff.dto.InscripcionResumenDTO;
import com.duoc.cursos.bff.dto.ProcesoInscripcionResponseDTO;
import com.duoc.cursos.bff.service.BffOrchestrationService;
import com.duoc.cursos.bff.service.EventoAcademicoPublisherService;
import com.duoc.cursos.bff.service.InscripcionService;
import com.duoc.cursos.bff.service.ResumenArchivoService;
import com.duoc.cursos.bff.service.S3StorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final BffOrchestrationService bffOrchestrationService;
    private final ResumenArchivoService resumenArchivoService;
    private final S3StorageService s3StorageService;
    private final EventoAcademicoPublisherService eventoAcademicoPublisherService;

    public InscripcionController(InscripcionService inscripcionService,
                                 BffOrchestrationService bffOrchestrationService,
                                 ResumenArchivoService resumenArchivoService,
                                 S3StorageService s3StorageService,
                                 EventoAcademicoPublisherService eventoAcademicoPublisherService) {
        this.inscripcionService = inscripcionService;
        this.bffOrchestrationService = bffOrchestrationService;
        this.resumenArchivoService = resumenArchivoService;
        this.s3StorageService = s3StorageService;
        this.eventoAcademicoPublisherService = eventoAcademicoPublisherService;
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResumenDTO>> listar() {
        return ResponseEntity.ok(inscripcionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResumenDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.obtenerPorId(id));
    }

    @PostMapping("/{inscripcionId}/enviar-mq")
    public ResponseEntity<EventoAcademicoResponseDTO> enviarResumenAMq(@PathVariable Long inscripcionId) {
        return ResponseEntity.ok(eventoAcademicoPublisherService.publicarInscripcionCreada(inscripcionId));
    }

    @PostMapping("/{inscripcionId}/generar-archivo")
    public ResponseEntity<ArchivoResumenResponseDTO> generarArchivo(@PathVariable Long inscripcionId) {
        return ResponseEntity.ok(resumenArchivoService.generarArchivo(inscripcionId));
    }

    @PostMapping("/{inscripcionId}/subir-s3")
    public ResponseEntity<ArchivoResumenResponseDTO> subirArchivoAS3(@PathVariable Long inscripcionId) {
        Path archivo = resumenArchivoService.obtenerArchivoLocalOGenerar(inscripcionId);
        return ResponseEntity.ok(s3StorageService.subirArchivo(inscripcionId, archivo));
    }

    @GetMapping("/{inscripcionId}/consultar-s3")
    public ResponseEntity<ArchivoResumenResponseDTO> consultarArchivoEnS3(@PathVariable Long inscripcionId) {
        inscripcionService.obtenerPorId(inscripcionId);
        return ResponseEntity.ok(s3StorageService.consultarArchivo(inscripcionId));
    }

    @GetMapping("/{inscripcionId}/descargar-s3")
    public ResponseEntity<Resource> descargarArchivoDesdeS3(@PathVariable Long inscripcionId) {
        inscripcionService.obtenerPorId(inscripcionId);

        byte[] contenido = s3StorageService.descargarArchivo(inscripcionId);
        String nombreArchivo = resumenArchivoService.obtenerNombreArchivo(inscripcionId);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(new ByteArrayResource(contenido));
    }

    @PostMapping
    public ResponseEntity<ProcesoInscripcionResponseDTO> crear(@Valid @RequestBody InscripcionRequestDTO inscripcionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bffOrchestrationService.registrarInscripcion(inscripcionRequestDTO));
    }
}
