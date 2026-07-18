package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ExamenRequestDTO;
import com.duoc.cursos.bff.dto.ExamenResponseDTO;
import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.model.Examen;
import com.duoc.cursos.bff.repository.ExamenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ExamenServiceTest {

    @Mock
    private ExamenRepository examenRepository;

    @Mock
    private CursoService cursoService;

    @Test
    void debeCrearExamenConDatosDelCurso() {
        ExamenService service = new ExamenService(examenRepository, cursoService);
        ExamenRequestDTO request = request();

        when(cursoService.obtenerEntidad(2L)).thenReturn(curso(2L));
        when(examenRepository.save(any(Examen.class))).thenAnswer(invocation -> {
            Examen examen = invocation.getArgument(0);
            examen.setId(10L);
            examen.setFechaCreacion(LocalDateTime.now());
            return examen;
        });

        ExamenResponseDTO response = service.crear(2L, request);

        assertEquals(10L, response.getId());
        assertEquals(2L, response.getCursoId());
        assertEquals("Evaluacion 1", response.getTitulo());
        assertEquals(new BigDecimal("100"), response.getPuntajeMaximo());
    }

    @Test
    void debeListarExamenesPorCurso() {
        ExamenService service = new ExamenService(examenRepository, cursoService);
        Examen examen = examen(4L, 2L);

        when(cursoService.obtenerEntidad(2L)).thenReturn(curso(2L));
        when(examenRepository.findByCursoIdOrderByIdAsc(2L)).thenReturn(List.of(examen));

        List<ExamenResponseDTO> response = service.listarPorCurso(2L);

        assertEquals(1, response.size());
        assertEquals("Evaluacion 1", response.get(0).getTitulo());
        verify(cursoService).obtenerEntidad(2L);
    }

    @Test
    void debeRetornarNotFoundCuandoNoExisteElExamen() {
        ExamenService service = new ExamenService(examenRepository, cursoService);

        when(examenRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.obtenerPorId(99L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("Examen no encontrado: 99", exception.getReason());
    }

    @Test
    void debeActualizarExamenExistente() {
        ExamenService service = new ExamenService(examenRepository, cursoService);
        Examen existente = examen(15L, 6L);
        ExamenRequestDTO request = request();
        request.setTitulo("Evaluacion final");

        when(examenRepository.findById(15L)).thenReturn(Optional.of(existente));
        when(examenRepository.save(any(Examen.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExamenResponseDTO response = service.actualizar(15L, request);

        assertEquals(15L, response.getId());
        assertEquals("Evaluacion final", response.getTitulo());
        assertEquals(6L, response.getCursoId());
    }

    private ExamenRequestDTO request() {
        ExamenRequestDTO request = new ExamenRequestDTO();
        request.setTitulo("Evaluacion 1");
        request.setDescripcion("Descripcion del examen");
        request.setPreguntasJson("[{\"pregunta\":\"P1\"}]");
        request.setPuntajeMaximo(new BigDecimal("100"));
        request.setActivo(Boolean.TRUE);
        return request;
    }

    private Curso curso(Long id) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNombre("Curso " + id);
        return curso;
    }

    private Examen examen(Long examenId, Long cursoId) {
        Examen examen = new Examen();
        examen.setId(examenId);
        examen.setCurso(curso(cursoId));
        examen.setTitulo("Evaluacion 1");
        examen.setDescripcion("Descripcion del examen");
        examen.setPreguntasJson("[{\"pregunta\":\"P1\"}]");
        examen.setPuntajeMaximo(new BigDecimal("100"));
        examen.setActivo(Boolean.TRUE);
        examen.setFechaCreacion(LocalDateTime.now());
        return examen;
    }
}
