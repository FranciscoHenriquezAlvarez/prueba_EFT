package com.duoc.eventos.academicos.consumidor.service;

import com.duoc.eventos.academicos.consumidor.dto.ConsumptionModeResponse;
import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoMensaje;
import com.duoc.eventos.academicos.consumidor.dto.ManualConsumptionResponse;
import com.duoc.eventos.academicos.consumidor.model.EventoAcademicoMq;
import com.duoc.eventos.academicos.consumidor.repository.EventoAcademicoMqRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoAcademicoConsumerServiceTest {

    @Mock
    private EventoAcademicoMqRepository eventoAcademicoMqRepository;

    @Mock
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MessageListenerContainer messageListenerContainer;

    private EventoAcademicoConsumerService eventoAcademicoConsumerService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        eventoAcademicoConsumerService = new EventoAcademicoConsumerService(
                eventoAcademicoMqRepository,
                rabbitListenerEndpointRegistry,
                rabbitTemplate,
                objectMapper,
                "eventos-academicos-exchange",
                "eventos-academicos-queue",
                "evento.academico.error"
        );

        when(rabbitListenerEndpointRegistry.getListenerContainer(EventoAcademicoListener.LISTENER_ID))
                .thenReturn(messageListenerContainer);
    }

    @Test
    void debeInformarModoAutomaticoCuandoListenerEstaActivo() {
        when(messageListenerContainer.isRunning()).thenReturn(true);

        ConsumptionModeResponse response = eventoAcademicoConsumerService.obtenerModoConsumo();

        assertEquals("AUTOMATICO", response.modo());
        assertTrue(response.consumoAutomaticoActivo());
        assertFalse(response.consumoManualDisponible());
        assertEquals("eventos-academicos-queue", response.colaPrincipal());
    }

    @Test
    void debeCambiarAModoManualDeteniendoListener() {
        ConsumptionModeResponse response = eventoAcademicoConsumerService.cambiarModoConsumo(false);

        verify(messageListenerContainer).stop();
        assertEquals("MANUAL", response.modo());
        assertFalse(response.consumoAutomaticoActivo());
        assertTrue(response.consumoManualDisponible());
    }

    @Test
    void debeBloquearConsumoManualSiModoAutomaticoEstaActivo() {
        when(messageListenerContainer.isRunning()).thenReturn(true);

        ManualConsumptionResponse response = eventoAcademicoConsumerService.consumirManualmente(1);

        assertEquals("NO_DISPONIBLE", response.estado());
        assertEquals("AUTOMATICO", response.modoActual());
        assertTrue(response.consumoAutomaticoActivo());
        assertFalse(response.consumoManualDisponible());
        verify(rabbitTemplate, never()).receiveAndConvert(
                any(String.class),
                any(Long.class),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void debeConsumirMensajeManualmenteUsandoLaMismaLogicaDeProcesamiento() {
        when(messageListenerContainer.isRunning()).thenReturn(false);
        when(rabbitTemplate.receiveAndConvert(
                eq("eventos-academicos-queue"),
                eq(0L),
                any(ParameterizedTypeReference.class)
        )).thenReturn(mensajeEjemplo());

        ManualConsumptionResponse response = eventoAcademicoConsumerService.consumirManualmente(1);

        ArgumentCaptor<EventoAcademicoMq> captor = ArgumentCaptor.forClass(EventoAcademicoMq.class);
        verify(eventoAcademicoMqRepository).save(captor.capture());
        EventoAcademicoMq entidadGuardada = captor.getValue();

        assertEquals("PROCESADO", response.estado());
        assertEquals(1, response.mensajesConsumidos());
        assertEquals("INSCRIPCION_CREADA", entidadGuardada.getTipoEvento());
        assertEquals("PROCESADO", entidadGuardada.getEstadoProcesamiento());
    }

    @Test
    void debeResponderSinMensajesCuandoLaColaEstaVaciaEnModoManual() {
        when(messageListenerContainer.isRunning()).thenReturn(false);
        when(rabbitTemplate.receiveAndConvert(
                eq("eventos-academicos-queue"),
                eq(0L),
                any(ParameterizedTypeReference.class)
        )).thenReturn(null);

        ManualConsumptionResponse response = eventoAcademicoConsumerService.consumirManualmente(2);

        assertEquals("SIN_MENSAJES", response.estado());
        assertEquals(0, response.mensajesConsumidos());
    }

    @Test
    void debeValidarCantidadMaximaEnConsumoManual() {
        when(messageListenerContainer.isRunning()).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventoAcademicoConsumerService.consumirManualmente(11)
        );

        assertEquals("El parametro cantidad debe estar entre 1 y 10.", exception.getMessage());
    }

    private EventoAcademicoMensaje mensajeEjemplo() {
        return new EventoAcademicoMensaje(
                "msg-123",
                "INSCRIPCION_CREADA",
                1L,
                22L,
                33L,
                null,
                null,
                LocalDateTime.of(2026, 7, 10, 11, 0),
                "ms-cursos-bff"
        );
    }
}
