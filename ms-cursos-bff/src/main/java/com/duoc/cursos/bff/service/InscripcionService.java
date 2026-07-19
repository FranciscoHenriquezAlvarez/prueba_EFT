package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.DetalleInscripcionResumenDTO;
import com.duoc.cursos.bff.dto.InscripcionRequestDTO;
import com.duoc.cursos.bff.dto.InscripcionResumenDTO;
import com.duoc.cursos.bff.exception.InscripcionNoEncontradaException;
import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.model.DetalleInscripcion;
import com.duoc.cursos.bff.model.Estudiante;
import com.duoc.cursos.bff.model.Inscripcion;
import com.duoc.cursos.bff.repository.CursoRepository;
import com.duoc.cursos.bff.repository.EstudianteRepository;
import com.duoc.cursos.bff.repository.InscripcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
// Servicio que registra inscripciones y consolida su resumen academico.
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              EstudianteRepository estudianteRepository,
                              CursoRepository cursoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<InscripcionResumenDTO> obtenerTodas() {
        return inscripcionRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertirAResumenDTO)
                .toList();
    }

    public InscripcionResumenDTO obtenerPorId(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new InscripcionNoEncontradaException(id));

        return convertirAResumenDTO(inscripcion);
    }

    // Valida estudiante y cursos antes de calcular el total de la inscripcion.
    public InscripcionResumenDTO guardar(InscripcionRequestDTO inscripcionRequestDTO) {
        Estudiante estudiante = estudianteRepository.findById(inscripcionRequestDTO.getEstudianteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        List<Curso> cursos = obtenerCursosSolicitados(inscripcionRequestDTO.getCursosIds());
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setEstado("REGISTRADA");

        List<DetalleInscripcion> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Curso curso : cursos) {
            if (inscripcionRepository.existsCursoInscrito(estudiante.getId(), curso.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El estudiante ya se encuentra inscrito en el curso " + curso.getNombre()
                );
            }

            DetalleInscripcion detalle = new DetalleInscripcion();
            detalle.setInscripcion(inscripcion);
            detalle.setCurso(curso);
            detalle.setCostoCurso(curso.getCosto());
            detalles.add(detalle);
            total = total.add(curso.getCosto());
        }

        inscripcion.setDetalles(detalles);
        inscripcion.setTotal(total);

        return convertirAResumenDTO(inscripcionRepository.save(inscripcion));
    }

    public boolean estaInscritoEnCurso(Long estudianteId, Long cursoId) {
        return inscripcionRepository.existsCursoInscrito(estudianteId, cursoId);
    }

    // Elimina ids repetidos y descarta cursos inexistentes o inactivos.
    private List<Curso> obtenerCursosSolicitados(List<Long> cursosIds) {
        if (cursosIds == null || cursosIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar al menos un curso");
        }

        Set<Long> idsUnicos = new LinkedHashSet<>(cursosIds);
        List<Curso> cursos = new ArrayList<>();
        for (Long cursoId : idsUnicos) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado: " + cursoId));
            if (Boolean.FALSE.equals(curso.getActivo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso se encuentra inactivo: " + curso.getNombre());
            }
            cursos.add(curso);
        }
        return cursos;
    }

    private InscripcionResumenDTO convertirAResumenDTO(Inscripcion inscripcion) {
        List<DetalleInscripcionResumenDTO> cursos = inscripcion.getDetalles()
                .stream()
                .map(detalle -> new DetalleInscripcionResumenDTO(
                        detalle.getCurso().getId(),
                        detalle.getCurso().getNombre(),
                        detalle.getCostoCurso()
                ))
                .toList();

        return new InscripcionResumenDTO(
                inscripcion.getId(),
                inscripcion.getEstudiante().getId(),
                inscripcion.getEstudiante().getNombre(),
                inscripcion.getEstudiante().getCorreo(),
                inscripcion.getFechaInscripcion(),
                cursos,
                inscripcion.getTotal(),
                inscripcion.getEstado()
        );
    }
}
