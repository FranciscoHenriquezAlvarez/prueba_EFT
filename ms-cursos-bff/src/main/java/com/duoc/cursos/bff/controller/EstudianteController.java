package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.EstudianteRequestDTO;
import com.duoc.cursos.bff.dto.EstudianteResponseDTO;
import com.duoc.cursos.bff.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> listar() {
        return ResponseEntity.ok(estudianteService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crear(@Valid @RequestBody EstudianteRequestDTO estudianteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.guardar(estudianteRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizar(@PathVariable Long id,
                                                            @Valid @RequestBody EstudianteRequestDTO estudianteRequestDTO) {
        return ResponseEntity.ok(estudianteService.actualizar(id, estudianteRequestDTO));
    }
}
