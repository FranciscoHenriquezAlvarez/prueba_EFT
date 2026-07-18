package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ContenidoCursoResponseDTO;
import com.duoc.cursos.bff.model.ContenidoCurso;
import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.model.Estudiante;
import com.duoc.cursos.bff.repository.ContenidoCursoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ContenidoCursoServiceTest {

    @Mock
    private ContenidoCursoRepository contenidoCursoRepository;

    @Mock
    private CursoService cursoService;

    @Mock
    private InscripcionService inscripcionService;

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private S3StorageService s3StorageService;

    @TempDir
    Path tempDir;

    @Test
    void debeCrearContenidoSinSubirAS3CuandoBucketNoEstaConfigurado() throws Exception {
        ContenidoCursoService service = new ContenidoCursoService(
                contenidoCursoRepository,
                cursoService,
                inscripcionService,
                estudianteService,
                s3StorageService,
                tempDir.toString()
        );

        when(cursoService.obtenerEntidad(7L)).thenReturn(curso(7L));
        when(s3StorageService.isBucketConfigured()).thenReturn(false);
        when(contenidoCursoRepository.save(any(ContenidoCurso.class))).thenAnswer(invocation -> {
            ContenidoCurso contenido = invocation.getArgument(0);
            contenido.setId(15L);
            contenido.setFechaCreacion(LocalDateTime.now());
            return contenido;
        });

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "clase.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        ContenidoCursoResponseDTO response = service.crear(7L, "Semana 1", "Material inicial", archivo);

        ArgumentCaptor<ContenidoCurso> captor = ArgumentCaptor.forClass(ContenidoCurso.class);
        verify(contenidoCursoRepository).save(captor.capture());
        ContenidoCurso entidad = captor.getValue();

        assertEquals(15L, response.getId());
        assertEquals("Semana 1", response.getTitulo());
        assertEquals("clase.pdf", response.getNombreArchivo());
        assertEquals("application/pdf", response.getTipoContenido());
        assertTrue(Files.exists(Path.of(entidad.getRutaTemporalEfs())));
    }

    @Test
    void debeRechazarArchivoVacio() {
        ContenidoCursoService service = new ContenidoCursoService(
                contenidoCursoRepository,
                cursoService,
                inscripcionService,
                estudianteService,
                s3StorageService,
                tempDir.toString()
        );

        MockMultipartFile archivo = new MockMultipartFile("archivo", "vacio.txt", "text/plain", new byte[0]);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.crear(1L, "Titulo", "Descripcion", archivo)
        );

        assertEquals(BAD_REQUEST, exception.getStatusCode());
        assertEquals("Debe adjuntar un archivo no vacio", exception.getReason());
    }

    @Test
    void debeBloquearListadoSiElEstudianteNoEstaInscrito() {
        ContenidoCursoService service = new ContenidoCursoService(
                contenidoCursoRepository,
                cursoService,
                inscripcionService,
                estudianteService,
                s3StorageService,
                tempDir.toString()
        );
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("alumno").build();

        when(estudianteService.resolverDesdeJwt(jwt)).thenReturn(estudiante(9L));
        when(inscripcionService.estaInscritoEnCurso(9L, 3L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.listarPorCurso(3L, jwt, false)
        );

        assertEquals(FORBIDDEN, exception.getStatusCode());
        assertEquals("Solo puede acceder a contenidos de cursos inscritos", exception.getReason());
    }

    @Test
    void debeRetornarNotFoundCuandoElContenidoNoTieneArchivoEnS3() {
        ContenidoCursoService service = new ContenidoCursoService(
                contenidoCursoRepository,
                cursoService,
                inscripcionService,
                estudianteService,
                s3StorageService,
                tempDir.toString()
        );
        ContenidoCurso contenido = contenido(20L, 4L);
        contenido.setKeyS3(null);

        when(contenidoCursoRepository.findById(20L)).thenReturn(Optional.of(contenido));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.descargar(20L, null, true)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        assertEquals("El contenido no dispone de un archivo en S3", exception.getReason());
    }

    @Test
    void debeEliminarArchivoEnS3CuandoExisteKeyYBucketConfigurado() {
        ContenidoCursoService service = new ContenidoCursoService(
                contenidoCursoRepository,
                cursoService,
                inscripcionService,
                estudianteService,
                s3StorageService,
                tempDir.toString()
        );
        ContenidoCurso contenido = contenido(30L, 5L);
        contenido.setKeyS3("cursos/5/contenidos/30/guia.pdf");

        when(contenidoCursoRepository.findById(30L)).thenReturn(Optional.of(contenido));
        when(s3StorageService.isBucketConfigured()).thenReturn(true);

        service.eliminar(30L);

        verify(s3StorageService).eliminarArchivoPorKey("cursos/5/contenidos/30/guia.pdf");
        verify(contenidoCursoRepository).delete(contenido);
    }

    private Curso curso(Long id) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNombre("Curso " + id);
        curso.setDescripcion("Descripcion");
        return curso;
    }

    private Estudiante estudiante(Long id) {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setNombre("Estudiante");
        estudiante.setCorreo("estudiante@email.com");
        return estudiante;
    }

    private ContenidoCurso contenido(Long contenidoId, Long cursoId) {
        ContenidoCurso contenido = new ContenidoCurso();
        contenido.setId(contenidoId);
        contenido.setCurso(curso(cursoId));
        contenido.setTitulo("Contenido");
        contenido.setDescripcion("Descripcion");
        contenido.setNombreArchivo("archivo.pdf");
        contenido.setTipoContenido("application/pdf");
        contenido.setRutaTemporalEfs("/tmp/archivo.pdf");
        contenido.setFechaCreacion(LocalDateTime.now());
        assertNotNull(contenido.getCurso());
        return contenido;
    }
}
