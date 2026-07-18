package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.dto.ContenidoCursoResponseDTO;
import com.duoc.cursos.bff.service.ContenidoCursoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ContenidoCursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContenidoCursoService contenidoCursoService;

    @Test
    void debeCrearContenidoConMultipart() throws Exception {
        when(contenidoCursoService.crear(eq(7L), eq("Semana 1"), eq("Material"), any())).thenReturn(contenido(11L, 7L));

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "clase.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenido".getBytes()
        );

        mockMvc.perform(multipart("/api/cursos/7/contenidos")
                        .file(archivo)
                        .param("titulo", "Semana 1")
                        .param("descripcion", "Material"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.cursoId").value(7))
                .andExpect(jsonPath("$.nombreArchivo").value("clase.pdf"));
    }

    @Test
    void debeListarContenidosDeUnCurso() throws Exception {
        when(contenidoCursoService.listarPorCurso(eq(7L), any(), eq(false))).thenReturn(List.of(contenido(11L, 7L)));

        mockMvc.perform(get("/api/cursos/7/contenidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Semana 1"));
    }

    @Test
    void debeDescargarContenidoConCabeceraAdjunta() throws Exception {
        when(contenidoCursoService.obtenerPorId(eq(11L), any(), eq(false))).thenReturn(contenido(11L, 7L));
        when(contenidoCursoService.descargar(eq(11L), any(), eq(false))).thenReturn("PDF".getBytes());

        mockMvc.perform(get("/api/contenidos/11/descargar"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"clase.pdf\""))
                .andExpect(content().bytes("PDF".getBytes()));
    }

    @Test
    void debeEliminarContenido() throws Exception {
        mockMvc.perform(delete("/api/contenidos/11"))
                .andExpect(status().isNoContent());

        verify(contenidoCursoService).eliminar(11L);
    }

    private ContenidoCursoResponseDTO contenido(Long id, Long cursoId) {
        return new ContenidoCursoResponseDTO(
                id,
                cursoId,
                "Semana 1",
                "Material",
                "clase.pdf",
                "application/pdf",
                "bucket",
                "key",
                "/tmp/clase.pdf",
                LocalDateTime.now()
        );
    }
}
