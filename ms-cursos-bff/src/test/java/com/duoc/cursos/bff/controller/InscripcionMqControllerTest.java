package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.EventoAcademicoResponseDTO;
import com.duoc.cursos.bff.service.EventoAcademicoPublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InscripcionMqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoAcademicoPublisherService eventoAcademicoPublisherService;

    @Test
    void debeInvocarElServicioAlEnviarResumenAMq() throws Exception {
        when(eventoAcademicoPublisherService.publicarInscripcionCreada(1L))
                .thenReturn(new EventoAcademicoResponseDTO(
                        "Evento enviado correctamente",
                        "msg-001",
                        "INSCRIPCION_CREADA",
                        1L,
                        null,
                        null
                ));

        mockMvc.perform(post("/api/inscripciones/1/enviar-mq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inscripcionId").value(1))
                .andExpect(jsonPath("$.tipoEvento").value("INSCRIPCION_CREADA"));

        verify(eventoAcademicoPublisherService).publicarInscripcionCreada(1L);
    }
}
