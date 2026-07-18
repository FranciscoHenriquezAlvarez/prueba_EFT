package com.duoc.eventos.academicos.consumidor.service;

import com.duoc.eventos.academicos.consumidor.dto.ConsumptionModeResponse;
import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoErrorMensaje;
import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoMensaje;
import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoMqResponse;
import com.duoc.eventos.academicos.consumidor.dto.ManualConsumptionResponse;
import com.duoc.eventos.academicos.consumidor.model.EventoAcademicoMq;
import com.duoc.eventos.academicos.consumidor.repository.EventoAcademicoMqRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoAcademicoConsumerService {

    private static final String ESTADO_PROCESADO = "PROCESADO";
    private static final String ESTADO_ERROR = "ERROR";
    private static final String MODO_AUTOMATICO = "AUTOMATICO";
    private static final String MODO_MANUAL = "MANUAL";
    private static final String ESTADO_NO_DISPONIBLE = "NO_DISPONIBLE";
    private static final String ESTADO_SIN_MENSAJES = "SIN_MENSAJES";
    private static final String ESTADO_PROCESADO_CON_ERRORES = "PROCESADO_CON_ERRORES";
    private static final int MAX_CONSUMO_MANUAL = 10;

    private final EventoAcademicoMqRepository eventoAcademicoMqRepository;
    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchange;
    private final String queueName;
    private final String errorRoutingKey;

    public EventoAcademicoConsumerService(EventoAcademicoMqRepository eventoAcademicoMqRepository,
                                          RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry,
                                          RabbitTemplate rabbitTemplate,
                                          ObjectMapper objectMapper,
                                          @Value("${app.rabbitmq.exchange}") String exchange,
                                          @Value("${app.rabbitmq.queue}") String queueName,
                                          @Value("${app.rabbitmq.error-routing-key}") String errorRoutingKey) {
        this.eventoAcademicoMqRepository = eventoAcademicoMqRepository;
        this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
        this.queueName = queueName;
        this.errorRoutingKey = errorRoutingKey;
    }

    @Transactional
    public ProcessingResult procesarMensaje(EventoAcademicoMensaje mensaje) {
        try {
            validarMensaje(mensaje);
            if (eventoAcademicoMqRepository.existsByMensajeId(mensaje.mensajeId())) {
                return new ProcessingResult(true, "DUPLICADO_IGNORADO");
            }

            EventoAcademicoMq entidad = new EventoAcademicoMq();
            entidad.setMensajeId(mensaje.mensajeId());
            entidad.setTipoEvento(mensaje.tipoEvento());
            entidad.setInscripcionId(mensaje.inscripcionId());
            entidad.setEstudianteId(mensaje.estudianteId());
            entidad.setCursoId(mensaje.cursoId());
            entidad.setExamenId(mensaje.examenId());
            entidad.setIntentoId(mensaje.intentoId());
            entidad.setFechaEvento(mensaje.fechaEvento());
            entidad.setServicioOrigen(mensaje.servicioOrigen());
            entidad.setPayloadJson(objectMapper.writeValueAsString(mensaje));
            entidad.setColaOrigen(queueName);
            entidad.setEstadoProcesamiento(ESTADO_PROCESADO);
            entidad.setFechaProcesamiento(LocalDateTime.now());
            eventoAcademicoMqRepository.save(entidad);
            return new ProcessingResult(true, null);
        } catch (Exception exception) {
            registrarError(mensaje, exception);
            return new ProcessingResult(false, exception.getMessage());
        }
    }

    public ConsumptionModeResponse obtenerModoConsumo() {
        return construirModoConsumoResponse(isAutomaticConsumptionActive());
    }

    public ConsumptionModeResponse cambiarModoConsumo(boolean automatico) {
        MessageListenerContainer container = getRequiredListenerContainer();
        if (automatico) {
            container.start();
            return construirModoConsumoResponse(
                    true,
                    "Consumo automatico activado. RabbitListener comenzara a procesar los mensajes pendientes en la cola principal."
            );
        }

        container.stop();
        return construirModoConsumoResponse(
                false,
                "Consumo automatico detenido. Ahora puede utilizar POST /api/mq/consumir para procesar mensajes manualmente."
        );
    }

    public ManualConsumptionResponse consumirManualmente(int cantidadSolicitada) {
        if (isAutomaticConsumptionActive()) {
            return new ManualConsumptionResponse(
                    ESTADO_NO_DISPONIBLE,
                    MODO_AUTOMATICO,
                    null,
                    null,
                    null,
                    null,
                    true,
                    false,
                    "El consumo manual no esta disponible porque el consumo automatico esta activo. Para usar este endpoint, primero cambie a modo manual con PUT /api/mq/modo-consumo?automatico=false."
            );
        }

        int cantidad = normalizarCantidad(cantidadSolicitada);
        int mensajesConsumidos = 0;
        int mensajesConError = 0;

        for (int indice = 0; indice < cantidad; indice++) {
            EventoAcademicoMensaje mensaje = receiveMessage();
            if (mensaje == null) {
                break;
            }

            mensajesConsumidos++;
            ProcessingResult processingResult = procesarMensaje(mensaje);
            if (!processingResult.exitoso()) {
                mensajesConError++;
            }
        }

        if (mensajesConsumidos == 0) {
            return new ManualConsumptionResponse(
                    ESTADO_SIN_MENSAJES,
                    MODO_MANUAL,
                    queueName,
                    cantidad,
                    0,
                    null,
                    false,
                    true,
                    "No existen mensajes disponibles en la cola principal."
            );
        }

        return new ManualConsumptionResponse(
                mensajesConError == 0 ? ESTADO_PROCESADO : ESTADO_PROCESADO_CON_ERRORES,
                MODO_MANUAL,
                queueName,
                cantidad,
                mensajesConsumidos,
                "EVENTOS_ACADEMICOS_MQ",
                false,
                true,
                mensajesConError == 0
                        ? "Consumo manual ejecutado correctamente."
                        : "Consumo manual ejecutado con errores controlados. Revise /api/mq/errores para mas detalle."
        );
    }

    public List<EventoAcademicoMqResponse> listarProcesados() {
        return eventoAcademicoMqRepository.findByEstadoProcesamientoOrderByFechaProcesamientoDesc(ESTADO_PROCESADO).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<EventoAcademicoMqResponse> listarErrores() {
        return eventoAcademicoMqRepository.findByEstadoProcesamientoOrderByFechaProcesamientoDesc(ESTADO_ERROR).stream()
                .map(this::toResponse)
                .toList();
    }

    private void validarMensaje(EventoAcademicoMensaje mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje recibido es nulo");
        }
        if (mensaje.mensajeId() == null || mensaje.mensajeId().isBlank()) {
            throw new IllegalArgumentException("El mensaje no contiene mensajeId");
        }
        if (mensaje.tipoEvento() == null || mensaje.tipoEvento().isBlank()) {
            throw new IllegalArgumentException("El mensaje no contiene tipoEvento");
        }
        if (mensaje.servicioOrigen() == null || mensaje.servicioOrigen().isBlank()) {
            throw new IllegalArgumentException("El mensaje no contiene servicioOrigen");
        }
        if (mensaje.inscripcionId() == null && mensaje.intentoId() == null && mensaje.examenId() == null) {
            throw new IllegalArgumentException("El mensaje no contiene un identificador academico valido");
        }
    }

    private void registrarError(EventoAcademicoMensaje mensaje, Exception exception) {
        String payloadOriginal = serializarSeguro(mensaje);
        EventoAcademicoMq entidad = new EventoAcademicoMq();
        entidad.setMensajeId(mensaje != null ? mensaje.mensajeId() : null);
        entidad.setTipoEvento(mensaje != null ? mensaje.tipoEvento() : "DESCONOCIDO");
        entidad.setInscripcionId(mensaje != null ? mensaje.inscripcionId() : null);
        entidad.setEstudianteId(mensaje != null ? mensaje.estudianteId() : null);
        entidad.setCursoId(mensaje != null ? mensaje.cursoId() : null);
        entidad.setExamenId(mensaje != null ? mensaje.examenId() : null);
        entidad.setIntentoId(mensaje != null ? mensaje.intentoId() : null);
        entidad.setFechaEvento(mensaje != null ? mensaje.fechaEvento() : null);
        entidad.setServicioOrigen(mensaje != null ? mensaje.servicioOrigen() : null);
        entidad.setPayloadJson(payloadOriginal);
        entidad.setColaOrigen(queueName);
        entidad.setEstadoProcesamiento(ESTADO_ERROR);
        entidad.setDetalleError(exception.getMessage());
        entidad.setFechaProcesamiento(LocalDateTime.now());
        eventoAcademicoMqRepository.save(entidad);

        EventoAcademicoErrorMensaje errorMensaje = new EventoAcademicoErrorMensaje(
                mensaje != null ? mensaje.mensajeId() : null,
                mensaje != null ? mensaje.tipoEvento() : null,
                queueName,
                payloadOriginal,
                exception.getMessage(),
                LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend(exchange, errorRoutingKey, errorMensaje);
    }

    private EventoAcademicoMensaje receiveMessage() {
        try {
            return rabbitTemplate.receiveAndConvert(
                    queueName,
                    0L,
                    new ParameterizedTypeReference<EventoAcademicoMensaje>() {
                    }
            );
        } catch (AmqpException exception) {
            throw new IllegalStateException("No fue posible consumir mensajes manualmente desde RabbitMQ.", exception);
        }
    }

    private int normalizarCantidad(int cantidadSolicitada) {
        if (cantidadSolicitada < 1 || cantidadSolicitada > MAX_CONSUMO_MANUAL) {
            throw new IllegalArgumentException("El parametro cantidad debe estar entre 1 y " + MAX_CONSUMO_MANUAL + ".");
        }
        return cantidadSolicitada;
    }

    private boolean isAutomaticConsumptionActive() {
        return getRequiredListenerContainer().isRunning();
    }

    private MessageListenerContainer getRequiredListenerContainer() {
        MessageListenerContainer container =
                rabbitListenerEndpointRegistry.getListenerContainer(EventoAcademicoListener.LISTENER_ID);
        if (container == null) {
            throw new IllegalStateException(
                    "No fue posible localizar el listener RabbitMQ con id " + EventoAcademicoListener.LISTENER_ID + "."
            );
        }
        return container;
    }

    private ConsumptionModeResponse construirModoConsumoResponse(boolean automaticoActivo) {
        String mensaje = automaticoActivo
                ? "El consumidor automatico se encuentra activo mediante RabbitListener."
                : "El consumidor automatico esta detenido. El consumo manual esta disponible mediante POST /api/mq/consumir.";
        return construirModoConsumoResponse(automaticoActivo, mensaje);
    }

    private ConsumptionModeResponse construirModoConsumoResponse(boolean automaticoActivo, String mensaje) {
        return new ConsumptionModeResponse(
                automaticoActivo ? MODO_AUTOMATICO : MODO_MANUAL,
                automaticoActivo,
                !automaticoActivo,
                EventoAcademicoListener.LISTENER_ID,
                queueName,
                mensaje
        );
    }

    private String serializarSeguro(EventoAcademicoMensaje mensaje) {
        if (mensaje == null) {
            return "{\"error\":\"mensaje nulo\"}";
        }
        try {
            return objectMapper.writeValueAsString(mensaje);
        } catch (JsonProcessingException exception) {
            return "{\"error\":\"no fue posible serializar el payload\"}";
        }
    }

    private EventoAcademicoMqResponse toResponse(EventoAcademicoMq entidad) {
        return new EventoAcademicoMqResponse(
                entidad.getId(),
                entidad.getMensajeId(),
                entidad.getTipoEvento(),
                entidad.getInscripcionId(),
                entidad.getEstudianteId(),
                entidad.getCursoId(),
                entidad.getExamenId(),
                entidad.getIntentoId(),
                entidad.getFechaEvento(),
                entidad.getServicioOrigen(),
                entidad.getPayloadJson(),
                entidad.getColaOrigen(),
                entidad.getEstadoProcesamiento(),
                entidad.getDetalleError(),
                entidad.getFechaProcesamiento()
        );
    }

    public record ProcessingResult(boolean exitoso, String detalle) {
    }
}
