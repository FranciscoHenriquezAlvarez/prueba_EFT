package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ExamenRequestDTO;
import com.duoc.cursos.bff.dto.ExamenResponseDTO;
import com.duoc.cursos.bff.model.Examen;
import com.duoc.cursos.bff.repository.ExamenRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ExamenService {

    private final ExamenRepository examenRepository;
    private final CursoService cursoService;

    public ExamenService(ExamenRepository examenRepository, CursoService cursoService) {
        this.examenRepository = examenRepository;
        this.cursoService = cursoService;
    }

    public ExamenResponseDTO crear(Long cursoId, ExamenRequestDTO requestDTO) {
        Examen examen = new Examen();
        examen.setCurso(cursoService.obtenerEntidad(cursoId));
        aplicarDatos(examen, requestDTO);
        return toResponse(examenRepository.save(examen));
    }

    public List<ExamenResponseDTO> listarPorCurso(Long cursoId) {
        cursoService.obtenerEntidad(cursoId);
        return examenRepository.findByCursoIdOrderByIdAsc(cursoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ExamenResponseDTO obtenerPorId(Long examenId) {
        return toResponse(obtenerEntidad(examenId));
    }

    public ExamenResponseDTO actualizar(Long examenId, ExamenRequestDTO requestDTO) {
        Examen examen = obtenerEntidad(examenId);
        aplicarDatos(examen, requestDTO);
        return toResponse(examenRepository.save(examen));
    }

    public Examen obtenerEntidad(Long examenId) {
        return examenRepository.findById(examenId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Examen no encontrado: " + examenId));
    }

    private void aplicarDatos(Examen examen, ExamenRequestDTO requestDTO) {
        examen.setTitulo(requestDTO.getTitulo());
        examen.setDescripcion(requestDTO.getDescripcion());
        examen.setPreguntasJson(requestDTO.getPreguntasJson());
        examen.setPuntajeMaximo(requestDTO.getPuntajeMaximo());
        examen.setActivo(requestDTO.getActivo());
    }

    private ExamenResponseDTO toResponse(Examen examen) {
        return new ExamenResponseDTO(
                examen.getId(),
                examen.getCurso().getId(),
                examen.getTitulo(),
                examen.getDescripcion(),
                examen.getPreguntasJson(),
                examen.getPuntajeMaximo(),
                examen.getActivo(),
                examen.getFechaCreacion()
        );
    }
}
