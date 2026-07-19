package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.ContenidoCursoResponseDTO;
import com.duoc.cursos.bff.service.ContenidoCursoService;
import com.duoc.cursos.bff.util.SecurityUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
// Controlador REST para publicar, consultar y descargar contenidos de curso.
public class ContenidoCursoController {

    private final ContenidoCursoService contenidoCursoService;

    public ContenidoCursoController(ContenidoCursoService contenidoCursoService) {
        this.contenidoCursoService = contenidoCursoService;
    }

    @PostMapping("/api/cursos/{cursoId}/contenidos")
    // Registra el contenido asociado a un curso y su archivo adjunto.
    public ResponseEntity<ContenidoCursoResponseDTO> crear(@PathVariable Long cursoId,
                                                           @RequestParam String titulo,
                                                           @RequestParam String descripcion,
                                                           @RequestParam MultipartFile archivo) {
        return ResponseEntity.status(201).body(contenidoCursoService.crear(cursoId, titulo, descripcion, archivo));
    }

    @GetMapping("/api/cursos/{cursoId}/contenidos")
    public ResponseEntity<List<ContenidoCursoResponseDTO>> listar(@PathVariable Long cursoId,
                                                                  Authentication authentication,
                                                                  @AuthenticationPrincipal Jwt jwt) {
        boolean profesor = SecurityUtils.hasRole(authentication, "PROFESOR");
        return ResponseEntity.ok(contenidoCursoService.listarPorCurso(cursoId, jwt, profesor));
    }

    @GetMapping("/api/contenidos/{contenidoId}")
    public ResponseEntity<ContenidoCursoResponseDTO> obtener(@PathVariable Long contenidoId,
                                                             Authentication authentication,
                                                             @AuthenticationPrincipal Jwt jwt) {
        boolean profesor = SecurityUtils.hasRole(authentication, "PROFESOR");
        return ResponseEntity.ok(contenidoCursoService.obtenerPorId(contenidoId, jwt, profesor));
    }

    @GetMapping("/api/contenidos/{contenidoId}/descargar")
    // Resuelve el acceso y devuelve el archivo almacenado para el curso.
    public ResponseEntity<Resource> descargar(@PathVariable Long contenidoId,
                                              Authentication authentication,
                                              @AuthenticationPrincipal Jwt jwt) {
        boolean profesor = SecurityUtils.hasRole(authentication, "PROFESOR");
        ContenidoCursoResponseDTO contenido = contenidoCursoService.obtenerPorId(contenidoId, jwt, profesor);
        byte[] archivo = contenidoCursoService.descargar(contenidoId, jwt, profesor);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + contenido.getNombreArchivo() + "\"")
                .body(new ByteArrayResource(archivo));
    }

    @DeleteMapping("/api/contenidos/{contenidoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long contenidoId) {
        contenidoCursoService.eliminar(contenidoId);
        return ResponseEntity.noContent().build();
    }
}
