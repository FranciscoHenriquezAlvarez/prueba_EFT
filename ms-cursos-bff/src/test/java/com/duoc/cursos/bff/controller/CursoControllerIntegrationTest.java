package com.duoc.cursos.bff.controller;

import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.repository.CursoRepository;
import com.duoc.cursos.bff.repository.DetalleInscripcionRepository;
import com.duoc.cursos.bff.repository.EstudianteRepository;
import com.duoc.cursos.bff.repository.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CursoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DetalleInscripcionRepository detalleInscripcionRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @BeforeEach
    void setUp() {
        // Limpia primero las tablas hijas para evitar errores de llave foranea
        detalleInscripcionRepository.deleteAll();

        // Luego limpia las tablas principales relacionadas
        inscripcionRepository.deleteAll();
        estudianteRepository.deleteAll();
        cursoRepository.deleteAll();
    }

    @Test
    void debeCrearCurso() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new CursoPayload(
                "Spring Boot Basico",
                "Curso introductorio",
                new BigDecimal("50000"),
                true
        ));

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Spring Boot Basico"))
                .andExpect(jsonPath("$.descripcion").value("Curso introductorio"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void debeListarCursosDisponibles() throws Exception {
        cursoRepository.save(crearCurso("Spring Boot Basico", "Curso 1", "50000"));
        cursoRepository.save(crearCurso("Docker Basico", "Curso 2", "40000"));

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Spring Boot Basico"))
                .andExpect(jsonPath("$[1].nombre").value("Docker Basico"));
    }

    private Curso crearCurso(String nombre, String descripcion, String costo) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setDescripcion(descripcion);
        curso.setCosto(new BigDecimal(costo));
        curso.setActivo(Boolean.TRUE);
        curso.setFechaCreacion(LocalDateTime.now());
        return curso;
    }

    private record CursoPayload(String nombre, String descripcion, BigDecimal costo, Boolean activo) {
    }
}
