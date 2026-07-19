package com.duoc.eventos.academicos.consumidor.service;

import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoMensaje;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
// Componente que escucha la cola principal y delega el procesamiento del evento.
public class EventoAcademicoListener {

    public static final String LISTENER_ID = "eventosAcademicosListener";

    private final EventoAcademicoConsumerService eventoAcademicoConsumerService;

    public EventoAcademicoListener(EventoAcademicoConsumerService eventoAcademicoConsumerService) {
        this.eventoAcademicoConsumerService = eventoAcademicoConsumerService;
    }

    @RabbitListener(
            id = LISTENER_ID,
            queues = "${app.rabbitmq.queue}",
            autoStartup = "${app.rabbitmq.consumo-automatico:true}"
    )
    // Mantiene el listener enfocado en la recepcion del mensaje.
    public void consumir(EventoAcademicoMensaje mensaje) {
        eventoAcademicoConsumerService.procesarMensaje(mensaje);
    }
}
