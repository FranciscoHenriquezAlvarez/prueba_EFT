package com.duoc.eventos.academicos.consumidor.controller;

import com.duoc.eventos.academicos.consumidor.config.SecurityRoles;
import com.duoc.eventos.academicos.consumidor.dto.ConsumptionModeResponse;
import com.duoc.eventos.academicos.consumidor.dto.EventoAcademicoMqResponse;
import com.duoc.eventos.academicos.consumidor.dto.ManualConsumptionResponse;
import com.duoc.eventos.academicos.consumidor.service.EventoAcademicoConsumerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// Pruebas que validan la seguridad de los endpoints de evidencia RabbitMQ.
class MqEvidenceSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoAcademicoConsumerService eventoAcademicoConsumerService;

    @Test
    void debeResponder401EnProcesadosSinToken() throws Exception {
        mockMvc.perform(get("/api/mq/procesados"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debePermitirProcesadosConRolProfesor() throws Exception {
        when(eventoAcademicoConsumerService.listarProcesados()).thenReturn(List.of(responseEjemplo("PROCESADO", null)));

        mockMvc.perform(get("/api/mq/procesados")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SecurityRoles.ROLE_PROFESOR))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoProcesamiento").value("PROCESADO"));

        verify(eventoAcademicoConsumerService).listarProcesados();
    }

    @Test
    void debePermitirErroresConRolProfesor() throws Exception {
        when(eventoAcademicoConsumerService.listarErrores()).thenReturn(List.of(responseEjemplo("ERROR", "Falla controlada")));

        mockMvc.perform(get("/api/mq/errores")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SecurityRoles.ROLE_PROFESOR))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].detalleError").value("Falla controlada"));

        verify(eventoAcademicoConsumerService).listarErrores();
    }

    @Test
    void debePermitirConsultarModoConsumoConRolProfesor() throws Exception {
        when(eventoAcademicoConsumerService.obtenerModoConsumo()).thenReturn(new ConsumptionModeResponse(
                "AUTOMATICO",
                true,
                false,
                "eventosAcademicosListener",
                "eventos-academicos-queue",
                "El consumidor automatico se encuentra activo mediante RabbitListener."
        ));

        mockMvc.perform(get("/api/mq/modo-consumo")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SecurityRoles.ROLE_PROFESOR))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modo").value("AUTOMATICO"))
                .andExpect(jsonPath("$.listenerId").value("eventosAcademicosListener"));

        verify(eventoAcademicoConsumerService).obtenerModoConsumo();
    }

    @Test
    void debePermitirCambiarModoConsumoConRolProfesor() throws Exception {
        when(eventoAcademicoConsumerService.cambiarModoConsumo(false)).thenReturn(new ConsumptionModeResponse(
                "MANUAL",
                false,
                true,
                "eventosAcademicosListener",
                "eventos-academicos-queue",
                "Consumo automatico detenido. Ahora puede utilizar POST /api/mq/consumir para procesar mensajes manualmente."
        ));

        mockMvc.perform(put("/api/mq/modo-consumo")
                        .param("automatico", "false")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SecurityRoles.ROLE_PROFESOR))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modo").value("MANUAL"))
                .andExpect(jsonPath("$.consumoManualDisponible").value(true));

        verify(eventoAcademicoConsumerService).cambiarModoConsumo(false);
    }

    @Test
    void debeResponder409CuandoConsumoManualNoEstaDisponible() throws Exception {
        when(eventoAcademicoConsumerService.consumirManualmente(1)).thenReturn(new ManualConsumptionResponse(
                "NO_DISPONIBLE",
                "AUTOMATICO",
                null,
                null,
                null,
                null,
                true,
                false,
                "El consumo manual no esta disponible porque el consumo automatico esta activo. Para usar este endpoint, primero cambie a modo manual con PUT /api/mq/modo-consumo?automatico=false."
        ));

        mockMvc.perform(post("/api/mq/consumir")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SecurityRoles.ROLE_PROFESOR))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value("NO_DISPONIBLE"))
                .andExpect(jsonPath("$.modoActual").value("AUTOMATICO"));

        verify(eventoAcademicoConsumerService).consumirManualmente(1);
    }

    private EventoAcademicoMqResponse responseEjemplo(String estado, String detalleError) {
        return new EventoAcademicoMqResponse(
                1L,
                "msg-123",
                "INSCRIPCION_CREADA",
                1L,
                22L,
                33L,
                null,
                null,
                LocalDateTime.of(2026, 7, 10, 11, 0),
                "ms-cursos-bff",
                "{\"mensajeId\":\"msg-123\"}",
                "eventos-academicos-queue",
                estado,
                detalleError,
                LocalDateTime.of(2026, 7, 10, 11, 0)
        );
    }
}
