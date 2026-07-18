package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ContenidoCursoResponseDTO;
import com.duoc.cursos.bff.exception.ArchivoLocalException;
import com.duoc.cursos.bff.model.ContenidoCurso;
import com.duoc.cursos.bff.model.Curso;
import com.duoc.cursos.bff.repository.ContenidoCursoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ContenidoCursoService {

    private final ContenidoCursoRepository contenidoCursoRepository;
    private final CursoService cursoService;
    private final InscripcionService inscripcionService;
    private final EstudianteService estudianteService;
    private final S3StorageService s3StorageService;
    private final Path contenidosPath;

    public ContenidoCursoService(ContenidoCursoRepository contenidoCursoRepository,
                                 CursoService cursoService,
                                 InscripcionService inscripcionService,
                                 EstudianteService estudianteService,
                                 S3StorageService s3StorageService,
                                 @Value("${app.archivos.contenidos-efs-path}") String contenidosPath) {
        this.contenidoCursoRepository = contenidoCursoRepository;
        this.cursoService = cursoService;
        this.inscripcionService = inscripcionService;
        this.estudianteService = estudianteService;
        this.s3StorageService = s3StorageService;
        this.contenidosPath = Paths.get(contenidosPath);
    }

    public ContenidoCursoResponseDTO crear(Long cursoId, String titulo, String descripcion, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe adjuntar un archivo no vacio");
        }
        if (!StringUtils.hasText(titulo) || !StringUtils.hasText(descripcion)) {
            throw new ResponseStatusException(BAD_REQUEST, "El titulo y la descripcion son obligatorios");
        }

        Curso curso = cursoService.obtenerEntidad(cursoId);
        Path rutaTemporal = guardarArchivoTemporal(cursoId, archivo);

        ContenidoCurso contenido = new ContenidoCurso();
        contenido.setCurso(curso);
        contenido.setTitulo(titulo);
        contenido.setDescripcion(descripcion);
        contenido.setNombreArchivo(archivo.getOriginalFilename());
        contenido.setTipoContenido(determinarTipoContenido(archivo));
        contenido.setRutaTemporalEfs(rutaTemporal.toAbsolutePath().toString());

        contenido = contenidoCursoRepository.save(contenido);

        if (s3StorageService.isBucketConfigured()) {
            var archivoS3 = s3StorageService.subirContenidoCurso(
                    cursoId,
                    contenido.getId(),
                    rutaTemporal,
                    archivo.getOriginalFilename(),
                    determinarTipoContenido(archivo)
            );
            contenido.setBucketS3(archivoS3.getBucket());
            contenido.setKeyS3(archivoS3.getKey());
            contenido = contenidoCursoRepository.save(contenido);
        }

        return toResponse(contenido);
    }

    public List<ContenidoCursoResponseDTO> listarPorCurso(Long cursoId, Jwt jwt, boolean profesor) {
        validarAcceso(cursoId, jwt, profesor);
        return contenidoCursoRepository.findByCursoIdOrderByIdAsc(cursoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ContenidoCursoResponseDTO obtenerPorId(Long contenidoId, Jwt jwt, boolean profesor) {
        ContenidoCurso contenido = obtenerEntidad(contenidoId);
        validarAcceso(contenido.getCurso().getId(), jwt, profesor);
        return toResponse(contenido);
    }

    public byte[] descargar(Long contenidoId, Jwt jwt, boolean profesor) {
        ContenidoCurso contenido = obtenerEntidad(contenidoId);
        validarAcceso(contenido.getCurso().getId(), jwt, profesor);
        if (!StringUtils.hasText(contenido.getKeyS3())) {
            throw new ResponseStatusException(NOT_FOUND, "El contenido no dispone de un archivo en S3");
        }
        return s3StorageService.descargarArchivoPorKey(contenido.getKeyS3());
    }

    public void eliminar(Long contenidoId) {
        ContenidoCurso contenido = obtenerEntidad(contenidoId);
        if (StringUtils.hasText(contenido.getKeyS3()) && s3StorageService.isBucketConfigured()) {
            s3StorageService.eliminarArchivoPorKey(contenido.getKeyS3());
        }
        contenidoCursoRepository.delete(contenido);
    }

    public ContenidoCurso obtenerEntidad(Long contenidoId) {
        return contenidoCursoRepository.findById(contenidoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Contenido no encontrado: " + contenidoId));
    }

    private void validarAcceso(Long cursoId, Jwt jwt, boolean profesor) {
        if (profesor) {
            return;
        }
        Long estudianteId = estudianteService.resolverDesdeJwt(jwt).getId();
        if (!inscripcionService.estaInscritoEnCurso(estudianteId, cursoId)) {
            throw new ResponseStatusException(FORBIDDEN, "Solo puede acceder a contenidos de cursos inscritos");
        }
    }

    private Path guardarArchivoTemporal(Long cursoId, MultipartFile archivo) {
        try {
            Files.createDirectories(contenidosPath);
            String nombreArchivo = archivo.getOriginalFilename();
            if (!StringUtils.hasText(nombreArchivo)) {
                throw new ResponseStatusException(BAD_REQUEST, "El archivo debe tener nombre");
            }
            Path destino = contenidosPath.resolve("curso-" + cursoId + "-" + System.currentTimeMillis() + "-" + nombreArchivo);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino;
        } catch (IOException | SecurityException exception) {
            throw new ArchivoLocalException("No fue posible guardar el archivo temporal en EFS", exception);
        }
    }

    private String determinarTipoContenido(MultipartFile archivo) {
        return StringUtils.hasText(archivo.getContentType()) ? archivo.getContentType() : "application/octet-stream";
    }

    private ContenidoCursoResponseDTO toResponse(ContenidoCurso contenido) {
        return new ContenidoCursoResponseDTO(
                contenido.getId(),
                contenido.getCurso().getId(),
                contenido.getTitulo(),
                contenido.getDescripcion(),
                contenido.getNombreArchivo(),
                contenido.getTipoContenido(),
                contenido.getBucketS3(),
                contenido.getKeyS3(),
                contenido.getRutaTemporalEfs(),
                contenido.getFechaCreacion()
        );
    }
}
