package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.EstudianteRequestDTO;
import com.duoc.cursos.bff.dto.EstudianteResponseDTO;
import com.duoc.cursos.bff.model.Estudiante;
import com.duoc.cursos.bff.repository.EstudianteRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    // Inyeccion del repositorio por constructor
    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    public List<EstudianteResponseDTO> obtenerTodos() {
        return estudianteRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public EstudianteResponseDTO obtenerPorId(Long id) {
        return convertirAResponseDTO(obtenerEntidad(id));
    }

    public EstudianteResponseDTO guardar(EstudianteRequestDTO estudianteRequestDTO) {
        validarDuplicados(null, estudianteRequestDTO);
        return convertirAResponseDTO(estudianteRepository.save(aplicarDatos(new Estudiante(), estudianteRequestDTO)));
    }

    public EstudianteResponseDTO actualizar(Long id, EstudianteRequestDTO estudianteRequestDTO) {
        Estudiante estudiante = obtenerEntidad(id);
        validarDuplicados(id, estudianteRequestDTO);
        return convertirAResponseDTO(estudianteRepository.save(aplicarDatos(estudiante, estudianteRequestDTO)));
    }

    public Estudiante obtenerEntidad(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Estudiante no encontrado: " + id));
    }

    public Estudiante resolverDesdeJwt(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(FORBIDDEN, "El token JWT es obligatorio para identificar al estudiante");
        }

        String subject = jwt.getSubject();
        String email = obtenerCorreoDesdeJwt(jwt);

        Optional<Estudiante> estudiante = Optional.empty();
        if (StringUtils.hasText(subject)) {
            estudiante = estudianteRepository.findByIdentificadorIdaasIgnoreCase(subject);
        }
        if (estudiante.isEmpty() && StringUtils.hasText(email)) {
            estudiante = estudianteRepository.findByCorreoIgnoreCase(email);
        }

        return estudiante.orElseThrow(() ->
                new ResponseStatusException(FORBIDDEN, "No fue posible relacionar el token con un estudiante registrado"));
    }

    public void validarAccesoPropio(Long estudianteId, Jwt jwt) {
        if (!resolverDesdeJwt(jwt).getId().equals(estudianteId)) {
            throw new ResponseStatusException(FORBIDDEN, "Solo puede consultar su propia informacion academica");
        }
    }

    private void validarDuplicados(Long id, EstudianteRequestDTO estudianteRequestDTO) {
        boolean correoDuplicado = id == null
                ? estudianteRepository.existsByCorreoIgnoreCase(estudianteRequestDTO.getCorreo())
                : estudianteRepository.existsByCorreoIgnoreCaseAndIdNot(estudianteRequestDTO.getCorreo(), id);
        if (correoDuplicado) {
            throw new ResponseStatusException(BAD_REQUEST, "El correo ya se encuentra registrado");
        }

        boolean idaasDuplicado = id == null
                ? estudianteRepository.existsByIdentificadorIdaasIgnoreCase(estudianteRequestDTO.getIdentificadorIdaas())
                : estudianteRepository.existsByIdentificadorIdaasIgnoreCaseAndIdNot(estudianteRequestDTO.getIdentificadorIdaas(), id);
        if (idaasDuplicado) {
            throw new ResponseStatusException(BAD_REQUEST, "El identificadorIdaas ya se encuentra registrado");
        }
    }

    private Estudiante aplicarDatos(Estudiante estudiante, EstudianteRequestDTO estudianteRequestDTO) {
        estudiante.setNombre(estudianteRequestDTO.getNombre());
        estudiante.setCorreo(estudianteRequestDTO.getCorreo());
        estudiante.setIdentificadorIdaas(estudianteRequestDTO.getIdentificadorIdaas());
        estudiante.setActivo(estudianteRequestDTO.getActivo());
        return estudiante;
    }

    private String obtenerCorreoDesdeJwt(Jwt jwt) {
        Object email = jwt.getClaim("email");
        if (email instanceof String text && StringUtils.hasText(text)) {
            return text;
        }
        Object emails = jwt.getClaim("emails");
        if (emails instanceof List<?> lista && !lista.isEmpty()) {
            Object first = lista.get(0);
            return first != null ? first.toString() : null;
        }
        return null;
    }

    private EstudianteResponseDTO convertirAResponseDTO(Estudiante estudiante) {
        return new EstudianteResponseDTO(
                estudiante.getId(),
                estudiante.getNombre(),
                estudiante.getCorreo(),
                estudiante.getIdentificadorIdaas(),
                estudiante.getActivo()
        );
    }
}
