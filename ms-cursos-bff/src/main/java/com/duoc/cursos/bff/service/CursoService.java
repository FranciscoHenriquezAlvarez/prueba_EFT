package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.CursoRequestDTO;
import com.duoc.cursos.bff.dto.CursoResponseDTO;
import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
// Servicio que concentra las operaciones principales de los cursos.
public class CursoService {

    private final CursoRepository cursoRepository;

    // Inyeccion del repositorio por constructor
    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<CursoResponseDTO> obtenerTodos() {
        return cursoRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public CursoResponseDTO obtenerPorId(Long id) {
        return convertirAResponseDTO(obtenerEntidad(id));
    }

    // Crea el curso con los datos recibidos por la API.
    public CursoResponseDTO guardar(CursoRequestDTO cursoRequestDTO) {
        return convertirAResponseDTO(cursoRepository.save(aplicarDatos(new Curso(), cursoRequestDTO)));
    }

    public CursoResponseDTO actualizar(Long id, CursoRequestDTO cursoRequestDTO) {
        Curso curso = obtenerEntidad(id);
        return convertirAResponseDTO(cursoRepository.save(aplicarDatos(curso, cursoRequestDTO)));
    }

    public void eliminar(Long id) {
        cursoRepository.delete(obtenerEntidad(id));
    }

    public Curso obtenerEntidad(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Curso no encontrado: " + id));
    }

    private Curso aplicarDatos(Curso curso, CursoRequestDTO cursoRequestDTO) {
        curso.setNombre(cursoRequestDTO.getNombre());
        curso.setDescripcion(cursoRequestDTO.getDescripcion());
        curso.setCosto(cursoRequestDTO.getCosto());
        curso.setActivo(cursoRequestDTO.getActivo());
        return curso;
    }

    private CursoResponseDTO convertirAResponseDTO(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getCosto(),
                curso.getActivo(),
                curso.getFechaCreacion()
        );
    }
}
