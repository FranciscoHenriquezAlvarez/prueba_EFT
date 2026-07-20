package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ArchivoResumenResponseDTO;
import com.duoc.cursos.bff.dto.EventoAcademicoResponseDTO;
import com.duoc.cursos.bff.dto.InscripcionRequestDTO;
import com.duoc.cursos.bff.dto.InscripcionResumenDTO;
import com.duoc.cursos.bff.dto.ProcesoInscripcionResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
// Servicio que centraliza el flujo academico coordinado por el BFF.
public class BffOrchestrationService {

    private final InscripcionService inscripcionService;
    private final ResumenArchivoService resumenArchivoService;
    private final S3StorageService s3StorageService;
    private final EventoAcademicoPublisherService eventoAcademicoPublisherService;
    private final RestClient.Builder restClientBuilder;
    private final String eventosConsumidorUrl;

    public BffOrchestrationService(InscripcionService inscripcionService,
                                   ResumenArchivoService resumenArchivoService,
                                   S3StorageService s3StorageService,
                                   EventoAcademicoPublisherService eventoAcademicoPublisherService,
                                   RestClient.Builder restClientBuilder,
                                   @Value("${app.eventos.consumidor-url}") String eventosConsumidorUrl) {
        this.inscripcionService = inscripcionService;
        this.resumenArchivoService = resumenArchivoService;
        this.s3StorageService = s3StorageService;
        this.eventoAcademicoPublisherService = eventoAcademicoPublisherService;
        this.restClientBuilder = restClientBuilder;
        this.eventosConsumidorUrl = eventosConsumidorUrl;
    }

    // Registra la inscripcion y encadena la generacion de sus evidencias.
    public ProcesoInscripcionResponseDTO registrarInscripcion(InscripcionRequestDTO requestDTO) {
        InscripcionResumenDTO inscripcion = inscripcionService.guardar(requestDTO);
        ArchivoResumenResponseDTO archivoResumen = resumenArchivoService.generarArchivo(inscripcion.getInscripcionId());

        ArchivoResumenResponseDTO archivoS3 = null;
        if (s3StorageService.isBucketConfigured()) {
            Path archivoLocal = resumenArchivoService.obtenerArchivoLocalOGenerar(inscripcion.getInscripcionId());
            archivoS3 = s3StorageService.subirArchivo(inscripcion.getInscripcionId(), archivoLocal);
        }

        EventoAcademicoResponseDTO eventoAcademico =
                eventoAcademicoPublisherService.publicarInscripcionCreada(inscripcion.getInscripcionId());

        return new ProcesoInscripcionResponseDTO(
                "Inscripcion registrada correctamente",
                inscripcion,
                archivoResumen,
                archivoS3,
                eventoAcademico
        );
    }

    // Reenvia el token para mantener la seguridad entre ambos microservicios.
    public Map<String, Object> solicitarConsumoManual(int cantidad, String authorizationHeader) {
        try {
            return restClientBuilder.baseUrl(eventosConsumidorUrl).build()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("/api/mq/consumir").queryParam("cantidad", cantidad).build())
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception exception) {
            throw new ResponseStatusException(BAD_GATEWAY, "No fue posible comunicarse con el consumidor");
        }
    }
}
