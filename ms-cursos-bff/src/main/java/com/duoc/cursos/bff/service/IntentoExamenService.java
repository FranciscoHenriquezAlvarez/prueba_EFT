package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.CalificacionRequestDTO;
import com.duoc.cursos.bff.dto.IntentoExamenRequestDTO;
import com.duoc.cursos.bff.dto.IntentoExamenResponseDTO;
import com.duoc.cursos.bff.model.Estudiante;
import com.duoc.cursos.bff.model.Examen;
import com.duoc.cursos.bff.model.IntentoExamen;
import com.duoc.cursos.bff.repository.IntentoExamenRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
// Servicio que registra intentos y calificaciones de examenes.
public class IntentoExamenService {

    private final IntentoExamenRepository intentoExamenRepository;
    private final ExamenService examenService;
    private final EstudianteService estudianteService;
    private final InscripcionService inscripcionService;
    private final EventoAcademicoPublisherService eventoAcademicoPublisherService;

    public IntentoExamenService(IntentoExamenRepository intentoExamenRepository,
                                ExamenService examenService,
                                EstudianteService estudianteService,
                                InscripcionService inscripcionService,
                                EventoAcademicoPublisherService eventoAcademicoPublisherService) {
        this.intentoExamenRepository = intentoExamenRepository;
        this.examenService = examenService;
        this.estudianteService = estudianteService;
        this.inscripcionService = inscripcionService;
        this.eventoAcademicoPublisherService = eventoAcademicoPublisherService;
    }

    // Valida que el estudiante este inscrito antes de registrar el intento.
    public IntentoExamenResponseDTO crear(Long examenId, IntentoExamenRequestDTO requestDTO, Jwt jwt) {
        Examen examen = examenService.obtenerEntidad(examenId);
        Estudiante estudiante = estudianteService.resolverDesdeJwt(jwt);
        if (!inscripcionService.estaInscritoEnCurso(estudiante.getId(), examen.getCurso().getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Solo puede rendir examenes de cursos inscritos");
        }

        IntentoExamen intento = new IntentoExamen();
        intento.setExamen(examen);
        intento.setEstudiante(estudiante);
        intento.setRespuestasJson(requestDTO.getRespuestasJson());
        intento.setEstado("ENVIADO");
        intento = intentoExamenRepository.save(intento);
        eventoAcademicoPublisherService.publicarExamenRealizado(intento.getId());
        return toResponse(intento);
    }

    public List<IntentoExamenResponseDTO> listarPorExamen(Long examenId) {
        examenService.obtenerEntidad(examenId);
        return intentoExamenRepository.findByExamenIdOrderByIdAsc(examenId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<IntentoExamenResponseDTO> listarPorEstudiante(Long estudianteId, Jwt jwt, boolean profesor) {
        if (!profesor) {
            estudianteService.validarAccesoPropio(estudianteId, jwt);
        } else {
            estudianteService.obtenerEntidad(estudianteId);
        }
        return intentoExamenRepository.findByEstudianteIdOrderByIdAsc(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    // Controla el puntaje y marca el intento como calificado.
    public IntentoExamenResponseDTO calificar(Long intentoId, CalificacionRequestDTO requestDTO) {
        IntentoExamen intento = intentoExamenRepository.findById(intentoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Intento no encontrado: " + intentoId));

        BigDecimal puntajeMaximo = intento.getExamen().getPuntajeMaximo();
        if (requestDTO.getPuntajeObtenido().compareTo(puntajeMaximo) > 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El puntaje no puede superar el puntaje maximo del examen");
        }

        intento.setPuntajeObtenido(requestDTO.getPuntajeObtenido());
        intento.setNota(requestDTO.getNota());
        intento.setObservacionProfesor(requestDTO.getObservacion());
        intento.setFechaCalificacion(LocalDateTime.now());
        intento.setEstado("CALIFICADO");

        intentoExamenRepository.save(intento);
        eventoAcademicoPublisherService.publicarCalificacionRegistrada(intento.getId());
        return toResponse(intento);
    }

    public List<IntentoExamenResponseDTO> listarCalificacionesPorEstudiante(Long estudianteId, Jwt jwt, boolean profesor) {
        return listarPorEstudiante(estudianteId, jwt, profesor).stream()
                .filter(intento -> "CALIFICADO".equals(intento.getEstado()))
                .toList();
    }

    public List<IntentoExamenResponseDTO> listarCalificacionesPorCurso(Long cursoId) {
        return intentoExamenRepository.findByExamenCursoIdOrderByIdAsc(cursoId).stream()
                .filter(intento -> "CALIFICADO".equals(intento.getEstado()))
                .map(this::toResponse)
                .toList();
    }

    private IntentoExamenResponseDTO toResponse(IntentoExamen intento) {
        return new IntentoExamenResponseDTO(
                intento.getId(),
                intento.getExamen().getId(),
                intento.getExamen().getCurso().getId(),
                intento.getEstudiante().getId(),
                intento.getEstudiante().getNombre(),
                intento.getRespuestasJson(),
                intento.getFechaRealizacion(),
                intento.getEstado(),
                intento.getPuntajeObtenido(),
                intento.getNota(),
                intento.getObservacionProfesor(),
                intento.getFechaCalificacion()
        );
    }
}
