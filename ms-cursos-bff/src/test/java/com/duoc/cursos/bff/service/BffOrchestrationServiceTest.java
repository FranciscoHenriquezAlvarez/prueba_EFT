package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ArchivoResumenResponseDTO;
import com.duoc.cursos.bff.dto.DetalleInscripcionResumenDTO;
import com.duoc.cursos.bff.dto.EventoAcademicoResponseDTO;
import com.duoc.cursos.bff.dto.InscripcionRequestDTO;
import com.duoc.cursos.bff.dto.InscripcionResumenDTO;
import com.duoc.cursos.bff.dto.ProcesoInscripcionResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith(MockitoExtension.class)
class BffOrchestrationServiceTest {

    @Mock
    private InscripcionService inscripcionService;

    @Mock
    private ResumenArchivoService resumenArchivoService;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private EventoAcademicoPublisherService eventoAcademicoPublisherService;

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @TempDir
    Path tempDir;

    @Test
    void debeRegistrarInscripcionConArchivoYEvento() throws Exception {
        BffOrchestrationService service = new BffOrchestrationService(
                inscripcionService,
                resumenArchivoService,
                s3StorageService,
                eventoAcademicoPublisherService,
                restClientBuilder,
                "http://consumer"
        );
        InscripcionRequestDTO request = new InscripcionRequestDTO();
        Path archivo = Files.createFile(tempDir.resolve("resumen-5.txt"));

        when(inscripcionService.guardar(request)).thenReturn(inscripcionResumen(5L));
        when(resumenArchivoService.generarArchivo(5L)).thenReturn(
                new ArchivoResumenResponseDTO("Archivo generado", archivo.toString(), "resumen-5.txt", null, null, true)
        );
        when(s3StorageService.isBucketConfigured()).thenReturn(true);
        when(resumenArchivoService.obtenerArchivoLocalOGenerar(5L)).thenReturn(archivo);
        when(s3StorageService.subirArchivo(5L, archivo)).thenReturn(
                new ArchivoResumenResponseDTO("Archivo subido", archivo.toString(), "resumen-5.txt", "bucket", "key", true)
        );
        when(eventoAcademicoPublisherService.publicarInscripcionCreada(5L)).thenReturn(
                new EventoAcademicoResponseDTO("Evento enviado", "msg-5", "INSCRIPCION_CREADA", 5L, null, null)
        );

        ProcesoInscripcionResponseDTO response = service.registrarInscripcion(request);

        assertEquals("Inscripcion registrada correctamente", response.getMensaje());
        assertEquals(5L, response.getInscripcion().getInscripcionId());
        assertNotNull(response.getArchivoS3());
        assertEquals("INSCRIPCION_CREADA", response.getEventoAcademico().getTipoEvento());
    }

    @Test
    void debeSolicitarConsumoManualAlConsumidor() {
        BffOrchestrationService service = new BffOrchestrationService(
                inscripcionService,
                resumenArchivoService,
                s3StorageService,
                eventoAcademicoPublisherService,
                restClientBuilder,
                "http://consumer"
        );

        when(restClientBuilder.baseUrl("http://consumer")).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.AUTHORIZATION, "Bearer token")).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(Map.of("estado", "PROCESADO"));

        Map<String, Object> response = service.solicitarConsumoManual(2, "Bearer token");

        assertEquals("PROCESADO", response.get("estado"));
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer token");
    }

    @Test
    void debeRetornarBadGatewaySiFallaLaComunicacionConElConsumidor() {
        BffOrchestrationService service = new BffOrchestrationService(
                inscripcionService,
                resumenArchivoService,
                s3StorageService,
                eventoAcademicoPublisherService,
                restClientBuilder,
                "http://consumer"
        );

        when(restClientBuilder.baseUrl("http://consumer")).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.AUTHORIZATION, "Bearer token")).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenThrow(new RuntimeException("down"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.solicitarConsumoManual(1, "Bearer token")
        );

        assertEquals(BAD_GATEWAY, exception.getStatusCode());
        assertEquals("No fue posible comunicarse con el consumidor academico", exception.getReason());
    }

    private InscripcionResumenDTO inscripcionResumen(Long id) {
        return new InscripcionResumenDTO(
                id,
                9L,
                "Ana Perez",
                "ana@email.com",
                LocalDateTime.now(),
                List.of(new DetalleInscripcionResumenDTO(2L, "Spring", java.math.BigDecimal.TEN)),
                java.math.BigDecimal.TEN,
                "REGISTRADA"
        );
    }
}
