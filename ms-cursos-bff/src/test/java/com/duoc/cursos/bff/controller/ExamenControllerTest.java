package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.ExamenResponseDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExamenService examenService;

    @MockitoBean
    private IntentoExamenService intentoExamenService;

    @Test
    void debeCrearExamen() throws Exception {
        when(examenService.crear(eq(3L), any())).thenReturn(examen(9L, 3L, "Evaluacion 1"));

        mockMvc.perform(post("/api/cursos/3/examenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Evaluacion 1",
                                  "descripcion": "Descripcion",
                                  "preguntasJson": "[{\\"pregunta\\":\\"P1\\"}]",
                                  "puntajeMaximo": 100,
                                  "activo": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.cursoId").value(3))
                .andExpect(jsonPath("$.titulo").value("Evaluacion 1"));
    }

    @Test
    void debeValidarRequestDeCreacionDeExamen() throws Exception {
        mockMvc.perform(post("/api/cursos/3/examenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "",
                                  "descripcion": "Descripcion",
                                  "preguntasJson": "[]",
                                  "puntajeMaximo": 0,
                                  "activo": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", anyOf(
                        containsString("titulo"),
                        containsString("puntajeMaximo")
                )))
                .andExpect(jsonPath("$.message", anyOf(
                        containsString("El titulo es obligatorio"),
                        containsString("El puntaje maximo debe ser mayor que cero")
                )));
    }

    @Test
    void debeListarExamenesPorCurso() throws Exception {
        when(examenService.listarPorCurso(3L)).thenReturn(List.of(examen(9L, 3L, "Evaluacion 1")));

        mockMvc.perform(get("/api/cursos/3/examenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Evaluacion 1"));
    }

    @Test
    void debeActualizarExamen() throws Exception {
        when(examenService.actualizar(eq(9L), any())).thenReturn(examen(9L, 3L, "Evaluacion actualizada"));

        mockMvc.perform(put("/api/examenes/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Evaluacion actualizada",
                                  "descripcion": "Descripcion",
                                  "preguntasJson": "[{\\"pregunta\\":\\"P1\\"}]",
                                  "puntajeMaximo": 100,
                                  "activo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Evaluacion actualizada"));

        verify(examenService).actualizar(eq(9L), any());
    }

    private ExamenResponseDTO examen(Long id, Long cursoId, String titulo) {
        return new ExamenResponseDTO(
                id,
                cursoId,
                titulo,
                "Descripcion",
                "[{\"pregunta\":\"P1\"}]",
                new BigDecimal("100"),
                Boolean.TRUE,
                LocalDateTime.now()
        );
    }
}
