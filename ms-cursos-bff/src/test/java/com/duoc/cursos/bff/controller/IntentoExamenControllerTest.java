package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.IntentoExamenResponseDTO;
import com.duoc.cursos.bff.service.ExamenService;
import com.duoc.cursos.bff.service.IntentoExamenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class IntentoExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExamenService examenService;

    @MockitoBean
    private IntentoExamenService intentoExamenService;

    @Test
    void debeCrearIntentoDeExamen() throws Exception {
        when(intentoExamenService.crear(eq(4L), any(), any())).thenReturn(intento(18L, "ENVIADO"));

        mockMvc.perform(post("/api/examenes/4/intentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "respuestasJson": "{\\"p1\\":\\"a\\"}"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(18))
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    void debeListarIntentosPorExamen() throws Exception {
        when(intentoExamenService.listarPorExamen(4L)).thenReturn(List.of(intento(18L, "ENVIADO")));

        mockMvc.perform(get("/api/examenes/4/intentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estudianteId").value(12));
    }

    @Test
    void debeValidarCalificacionInvalida() throws Exception {
        mockMvc.perform(put("/api/intentos/18/calificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "puntajeObtenido": -1,
                                  "nota": 8.0,
                                  "observacion": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", anyOf(
                        containsString("observacion"),
                        containsString("puntajeObtenido"),
                        containsString("nota")
                )))
                .andExpect(jsonPath("$.message", anyOf(
                        containsString("La observacion es obligatoria"),
                        containsString("El puntaje no puede ser negativo"),
                        containsString("La nota debe ser menor o igual a 7.0")
                )));
    }

    @Test
    void debeListarCalificacionesPorEstudiante() throws Exception {
        when(intentoExamenService.listarCalificacionesPorEstudiante(eq(12L), any(), eq(false)))
                .thenReturn(List.of(intento(18L, "CALIFICADO")));

        mockMvc.perform(get("/api/estudiantes/12/calificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("CALIFICADO"));
    }

    private IntentoExamenResponseDTO intento(Long id, String estado) {
        return new IntentoExamenResponseDTO(
                id,
                4L,
                3L,
                12L,
                "Ana Perez",
                "{\"p1\":\"a\"}",
                LocalDateTime.now(),
                estado,
                new BigDecimal("95"),
                new BigDecimal("6.5"),
                "Buen trabajo",
                LocalDateTime.now()
        );
    }
}
