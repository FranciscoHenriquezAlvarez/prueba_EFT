package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.EventoAcademicoMensaje;
import com.duoc.cursos.bff.dto.EventoAcademicoResponseDTO;
import com.duoc.cursos.bff.model.Inscripcion;
import com.duoc.cursos.bff.model.IntentoExamen;
import com.duoc.cursos.bff.repository.InscripcionRepository;
import com.duoc.cursos.bff.repository.IntentoExamenRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
// Servicio que publica eventos academicos persistentes en RabbitMQ.
public class EventoAcademicoPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final InscripcionRepository inscripcionRepository;
    private final IntentoExamenRepository intentoExamenRepository;
    private final String exchangeName;
    private final String routingKey;

    public EventoAcademicoPublisherService(RabbitTemplate rabbitTemplate,
                                           InscripcionRepository inscripcionRepository,
                                           IntentoExamenRepository intentoExamenRepository,
                                           @Value("${app.rabbitmq.exchange}") String exchangeName,
                                           @Value("${app.rabbitmq.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.inscripcionRepository = inscripcionRepository;
        this.intentoExamenRepository = intentoExamenRepository;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    // Emite el evento base de la inscripcion con los ids academicos disponibles.
    public EventoAcademicoResponseDTO publicarInscripcionCreada(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Inscripcion no encontrada: " + inscripcionId));

        Long cursoId = inscripcion.getDetalles().isEmpty() ? null : inscripcion.getDetalles().get(0).getCurso().getId();
        EventoAcademicoMensaje mensaje = new EventoAcademicoMensaje(
                UUID.randomUUID().toString(),
                "INSCRIPCION_CREADA",
                inscripcion.getId(),
                inscripcion.getEstudiante().getId(),
                cursoId,
                null,
                null,
                LocalDateTime.now(),
                "ms-cursos-bff"
        );
        rabbitTemplate.convertAndSend(exchangeName, routingKey, mensaje);
        return new EventoAcademicoResponseDTO(
                "Evento enviado correctamente",
                mensaje.getMensajeId(),
                mensaje.getTipoEvento(),
                mensaje.getInscripcionId(),
                null,
                null
        );
    }

    public void publicarExamenRealizado(Long intentoId) {
        publicarEventoIntento(intentoId, "EXAMEN_REALIZADO");
    }

    public void publicarCalificacionRegistrada(Long intentoId) {
        publicarEventoIntento(intentoId, "CALIFICACION_REGISTRADA");
    }

    // Reutiliza la estructura para intentos y calificaciones enviadas por MQ.
    private void publicarEventoIntento(Long intentoId, String tipoEvento) {
        IntentoExamen intento = intentoExamenRepository.findById(intentoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Intento no encontrado: " + intentoId));
        EventoAcademicoMensaje mensaje = new EventoAcademicoMensaje(
                UUID.randomUUID().toString(),
                tipoEvento,
                null,
                intento.getEstudiante().getId(),
                intento.getExamen().getCurso().getId(),
                intento.getExamen().getId(),
                intento.getId(),
                LocalDateTime.now(),
                "ms-cursos-bff"
        );
        rabbitTemplate.convertAndSend(exchangeName, routingKey, mensaje);
    }
}
