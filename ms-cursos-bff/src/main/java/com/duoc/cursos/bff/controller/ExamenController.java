package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.CalificacionRequestDTO;
import com.duoc.cursos.bff.dto.ExamenRequestDTO;
import com.duoc.cursos.bff.dto.ExamenResponseDTO;
import com.duoc.cursos.bff.dto.IntentoExamenRequestDTO;
import com.duoc.cursos.bff.dto.IntentoExamenResponseDTO;
import com.duoc.cursos.bff.service.ExamenService;
import com.duoc.cursos.bff.service.IntentoExamenService;
import com.duoc.cursos.bff.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class ExamenController {

    private final ExamenService examenService;
    private final IntentoExamenService intentoExamenService;

    public ExamenController(ExamenService examenService, IntentoExamenService intentoExamenService) {
        this.examenService = examenService;
        this.intentoExamenService = intentoExamenService;
    }

    @PostMapping("/api/cursos/{cursoId}/examenes")
    public ResponseEntity<ExamenResponseDTO> crearExamen(@PathVariable Long cursoId,
                                                         @Valid @RequestBody ExamenRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examenService.crear(cursoId, requestDTO));
    }

    @GetMapping("/api/cursos/{cursoId}/examenes")
    public ResponseEntity<List<ExamenResponseDTO>> listarExamenes(@PathVariable Long cursoId) {
        return ResponseEntity.ok(examenService.listarPorCurso(cursoId));
    }

    @GetMapping("/api/examenes/{examenId}")
    public ResponseEntity<ExamenResponseDTO> obtenerExamen(@PathVariable Long examenId) {
        return ResponseEntity.ok(examenService.obtenerPorId(examenId));
    }

    @PutMapping("/api/examenes/{examenId}")
    public ResponseEntity<ExamenResponseDTO> actualizarExamen(@PathVariable Long examenId,
                                                              @Valid @RequestBody ExamenRequestDTO requestDTO) {
        return ResponseEntity.ok(examenService.actualizar(examenId, requestDTO));
    }

    @PostMapping("/api/examenes/{examenId}/intentos")
    public ResponseEntity<IntentoExamenResponseDTO> crearIntento(@PathVariable Long examenId,
                                                                 @Valid @RequestBody IntentoExamenRequestDTO requestDTO,
                                                                 @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(intentoExamenService.crear(examenId, requestDTO, jwt));
    }

    @GetMapping("/api/examenes/{examenId}/intentos")
    public ResponseEntity<List<IntentoExamenResponseDTO>> listarIntentos(@PathVariable Long examenId) {
        return ResponseEntity.ok(intentoExamenService.listarPorExamen(examenId));
    }

    @GetMapping("/api/estudiantes/{estudianteId}/intentos")
    public ResponseEntity<List<IntentoExamenResponseDTO>> listarIntentosPorEstudiante(@PathVariable Long estudianteId,
                                                                                       Authentication authentication,
                                                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(intentoExamenService.listarPorEstudiante(
                estudianteId,
                jwt,
                SecurityUtils.hasRole(authentication, "PROFESOR")
        ));
    }

    @PutMapping("/api/intentos/{intentoId}/calificacion")
    public ResponseEntity<IntentoExamenResponseDTO> calificar(@PathVariable Long intentoId,
                                                              @Valid @RequestBody CalificacionRequestDTO requestDTO) {
        return ResponseEntity.ok(intentoExamenService.calificar(intentoId, requestDTO));
    }

    @GetMapping("/api/estudiantes/{estudianteId}/calificaciones")
    public ResponseEntity<List<IntentoExamenResponseDTO>> listarCalificacionesEstudiante(@PathVariable Long estudianteId,
                                                                                          Authentication authentication,
                                                                                          @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(intentoExamenService.listarCalificacionesPorEstudiante(
                estudianteId,
                jwt,
                SecurityUtils.hasRole(authentication, "PROFESOR")
        ));
    }

    @GetMapping("/api/cursos/{cursoId}/calificaciones")
    public ResponseEntity<List<IntentoExamenResponseDTO>> listarCalificacionesCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(intentoExamenService.listarCalificacionesPorCurso(cursoId));
    }
}
