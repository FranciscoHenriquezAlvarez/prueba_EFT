package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.CalificacionRequestDTO;
import com.duoc.cursos.bff.dto.IntentoExamenRequestDTO;
import com.duoc.cursos.bff.dto.IntentoExamenResponseDTO;
import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.model.Estudiante;
import com.duoc.cursos.bff.model.Examen;
import com.duoc.cursos.bff.model.IntentoExamen;
import com.duoc.cursos.bff.repository.IntentoExamenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class IntentoExamenServiceTest {

    @Mock
    private IntentoExamenRepository intentoExamenRepository;

    @Mock
    private ExamenService examenService;

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private InscripcionService inscripcionService;

    @Mock
    private EventoAcademicoPublisherService eventoAcademicoPublisherService;

    @Test
    void debeCrearIntentoYPublicarEvento() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("alumno").build();
        IntentoExamenRequestDTO request = new IntentoExamenRequestDTO();
        request.setRespuestasJson("{\"p1\":\"a\"}");

        when(examenService.obtenerEntidad(3L)).thenReturn(examen(3L, 8L, "100"));
        when(estudianteService.resolverDesdeJwt(jwt)).thenReturn(estudiante(11L));
        when(inscripcionService.estaInscritoEnCurso(11L, 8L)).thenReturn(true);
        when(intentoExamenRepository.save(any(IntentoExamen.class))).thenAnswer(invocation -> {
            IntentoExamen intento = invocation.getArgument(0);
            intento.setId(17L);
            intento.setFechaRealizacion(LocalDateTime.now());
            return intento;
        });

        IntentoExamenResponseDTO response = service.crear(3L, request, jwt);

        assertEquals(17L, response.getId());
        assertEquals("ENVIADO", response.getEstado());
        assertEquals(11L, response.getEstudianteId());
        verify(eventoAcademicoPublisherService).publicarExamenRealizado(17L);
    }

    @Test
    void debeBloquearIntentoSiElEstudianteNoEstaInscrito() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("alumno").build();
        IntentoExamenRequestDTO request = new IntentoExamenRequestDTO();
        request.setRespuestasJson("{\"p1\":\"a\"}");

        when(examenService.obtenerEntidad(3L)).thenReturn(examen(3L, 8L, "100"));
        when(estudianteService.resolverDesdeJwt(jwt)).thenReturn(estudiante(11L));
        when(inscripcionService.estaInscritoEnCurso(11L, 8L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.crear(3L, request, jwt)
        );

        assertEquals(FORBIDDEN, exception.getStatusCode());
        assertEquals("Solo puede rendir examenes de cursos inscritos", exception.getReason());
    }

    @Test
    void debeCalificarIntentoYPublicarEvento() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        IntentoExamen intento = intento(21L, "100");
        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setPuntajeObtenido(new BigDecimal("95"));
        request.setNota(new BigDecimal("6.5"));
        request.setObservacion("Buen resultado");

        when(intentoExamenRepository.findById(21L)).thenReturn(Optional.of(intento));
        when(intentoExamenRepository.save(any(IntentoExamen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntentoExamenResponseDTO response = service.calificar(21L, request);

        assertEquals("CALIFICADO", response.getEstado());
        assertEquals(new BigDecimal("95"), response.getPuntajeObtenido());
        assertEquals(new BigDecimal("6.5"), response.getNota());
        assertNotNull(response.getFechaCalificacion());
        verify(eventoAcademicoPublisherService).publicarCalificacionRegistrada(21L);
    }

    @Test
    void debeCalificarSinDependerDeLaInstanciaRetornadaPorSave() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        IntentoExamen intentoCargado = intento(21L, "100");
        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setPuntajeObtenido(new BigDecimal("85"));
        request.setNota(new BigDecimal("6.2"));
        request.setObservacion("Buen desarrollo de los contenidos evaluados");

        when(intentoExamenRepository.findById(21L)).thenReturn(Optional.of(intentoCargado));
        when(intentoExamenRepository.save(any(IntentoExamen.class))).thenAnswer(invocation -> {
            IntentoExamen intentoGuardado = new IntentoExamen();
            intentoGuardado.setId(((IntentoExamen) invocation.getArgument(0)).getId());
            intentoGuardado.setEstado(((IntentoExamen) invocation.getArgument(0)).getEstado());
            return intentoGuardado;
        });

        IntentoExamenResponseDTO response = service.calificar(21L, request);

        assertEquals(21L, response.getId());
        assertEquals(3L, response.getExamenId());
        assertEquals(8L, response.getCursoId());
        assertEquals(11L, response.getEstudianteId());
        assertEquals("Ana", response.getNombreEstudiante());
        assertEquals("CALIFICADO", response.getEstado());
        assertEquals(new BigDecimal("85"), response.getPuntajeObtenido());
        assertEquals(new BigDecimal("6.2"), response.getNota());
        assertEquals("Buen desarrollo de los contenidos evaluados", response.getObservacionProfesor());
        assertNotNull(response.getFechaCalificacion());

        verify(intentoExamenRepository, times(1)).save(argThat(intento ->
                "CALIFICADO".equals(intento.getEstado())
                        && new BigDecimal("85").compareTo(intento.getPuntajeObtenido()) == 0
                        && new BigDecimal("6.2").compareTo(intento.getNota()) == 0
                        && "Buen desarrollo de los contenidos evaluados".equals(intento.getObservacionProfesor())
                        && intento.getFechaCalificacion() != null
        ));
        verify(eventoAcademicoPublisherService, times(1)).publicarCalificacionRegistrada(21L);
    }

    @Test
    void debeRechazarPuntajeSuperiorAlMaximo() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        IntentoExamen intento = intento(21L, "50");
        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setPuntajeObtenido(new BigDecimal("60"));
        request.setNota(new BigDecimal("5.0"));
        request.setObservacion("Observacion");

        when(intentoExamenRepository.findById(21L)).thenReturn(Optional.of(intento));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.calificar(21L, request)
        );

        assertEquals(BAD_REQUEST, exception.getStatusCode());
        assertEquals("El puntaje no puede superar el puntaje maximo del examen", exception.getReason());
    }

    @Test
    void debeFiltrarSoloCalificadosAlListarPorCurso() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        IntentoExamen calificado = intento(1L, "100");
        calificado.setEstado("CALIFICADO");
        IntentoExamen enviado = intento(2L, "100");
        enviado.setEstado("ENVIADO");

        when(intentoExamenRepository.findByExamenCursoIdOrderByIdAsc(8L)).thenReturn(List.of(calificado, enviado));

        List<IntentoExamenResponseDTO> response = service.listarCalificacionesPorCurso(8L);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
    }

    @Test
    void debeRetornarNotFoundSiElIntentoNoExiste() {
        IntentoExamenService service = new IntentoExamenService(
                intentoExamenRepository,
                examenService,
                estudianteService,
                inscripcionService,
                eventoAcademicoPublisherService
        );
        CalificacionRequestDTO request = new CalificacionRequestDTO();
        request.setPuntajeObtenido(new BigDecimal("10"));
        request.setNota(new BigDecimal("4.0"));
        request.setObservacion("Observacion");

        when(intentoExamenRepository.findById(404L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.calificar(404L, request)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("Intento no encontrado: 404", exception.getReason());
    }

    private Examen examen(Long examenId, Long cursoId, String puntajeMaximo) {
        Examen examen = new Examen();
        examen.setId(examenId);
        Curso curso = new Curso();
        curso.setId(cursoId);
        curso.setNombre("Curso " + cursoId);
        examen.setCurso(curso);
        examen.setTitulo("Examen");
        examen.setPuntajeMaximo(new BigDecimal(puntajeMaximo));
        return examen;
    }

    private Estudiante estudiante(Long id) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setNombre("Ana");
        estudiante.setCorreo("ana@email.com");
        return estudiante;
    }

    private IntentoExamen intento(Long intentoId, String puntajeMaximo) {
        IntentoExamen intento = new IntentoExamen();
        intento.setId(intentoId);
        intento.setExamen(examen(3L, 8L, puntajeMaximo));
        intento.setEstudiante(estudiante(11L));
        intento.setRespuestasJson("{\"p1\":\"a\"}");
        intento.setFechaRealizacion(LocalDateTime.now());
        intento.setEstado("ENVIADO");
        return intento;
    }
}
