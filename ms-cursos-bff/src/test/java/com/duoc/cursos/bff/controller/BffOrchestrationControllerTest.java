package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.service.BffOrchestrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BffOrchestrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BffOrchestrationService bffOrchestrationService;

    @Test
    void debeDelegarConsumoManualAlServicio() throws Exception {
        when(bffOrchestrationService.solicitarConsumoManual(2, "Bearer token"))
                .thenReturn(Map.of("estado", "PROCESADO", "mensajesConsumidos", 2));

        mockMvc.perform(post("/api/bff/eventos/consumir")
                        .queryParam("cantidad", "2")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PROCESADO"))
                .andExpect(jsonPath("$.mensajesConsumidos").value(2));

        verify(bffOrchestrationService).solicitarConsumoManual(2, "Bearer token");
    }
}
