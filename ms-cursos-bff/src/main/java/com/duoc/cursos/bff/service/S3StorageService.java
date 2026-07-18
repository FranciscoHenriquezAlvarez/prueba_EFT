package com.duoc.cursos.bff.service;

import com.duoc.cursos.bff.dto.ArchivoResumenResponseDTO;
import com.duoc.cursos.bff.exception.ArchivoNoEncontradoException;
import com.duoc.cursos.bff.exception.S3StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(S3Client s3Client,
                            @Value("${aws.s3.bucket-name:}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public ArchivoResumenResponseDTO subirArchivo(Long inscripcionId, Path archivo) {
        String key = construirKey(inscripcionId);
        subirArchivo(archivo, key, "text/plain");

        return new ArchivoResumenResponseDTO(
                "Archivo subido correctamente a S3",
                archivo.toAbsolutePath().toString(),
                archivo.getFileName().toString(),
                bucketName,
                key,
                Boolean.TRUE
        );
    }

    public ArchivoResumenResponseDTO subirContenidoCurso(Long cursoId, Long contenidoId, Path archivo, String nombreArchivo,
                                                         String contentType) {
        String key = construirKeyContenido(cursoId, contenidoId, nombreArchivo);
        subirArchivo(archivo, key, contentType);

        return new ArchivoResumenResponseDTO(
                "Contenido subido correctamente a S3",
                archivo.toAbsolutePath().toString(),
                archivo.getFileName().toString(),
                bucketName,
                key,
                Boolean.TRUE
        );
    }

    public void subirArchivo(Path archivo, String key, String contentType) {
        validarBucketConfigurado();
        if (archivo == null || !Files.exists(archivo)) {
            throw new ArchivoNoEncontradoException("El archivo local no existe");
        }

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromFile(archivo)
            );
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para subir el archivo", exception);
        } catch (S3Exception exception) {
            throw crearErrorOperacionS3("No fue posible subir el archivo a S3", exception);
        }

    }

    public ArchivoResumenResponseDTO reemplazarArchivo(Long inscripcionId, Path archivo) {
        boolean existeAntes = existeArchivo(inscripcionId);
        ArchivoResumenResponseDTO respuesta = subirArchivo(inscripcionId, archivo);
        respuesta.setMensaje(existeAntes
                ? "Archivo reemplazado correctamente en S3"
                : "El archivo no existia en S3 y fue creado correctamente");
        return respuesta;
    }

    public ArchivoResumenResponseDTO consultarArchivo(Long inscripcionId) {
        validarBucketConfigurado();
        boolean existe = existeArchivo(inscripcionId);

        return new ArchivoResumenResponseDTO(
                existe ? "Archivo encontrado en S3" : "Archivo no encontrado en S3",
                null,
                "resumen-" + inscripcionId + ".txt",
                bucketName,
                construirKey(inscripcionId),
                existe
        );
    }

    public byte[] descargarArchivo(Long inscripcionId) {
        return descargarArchivoPorKey(construirKey(inscripcionId));
    }

    public byte[] descargarArchivoPorKey(String key) {
        validarBucketConfigurado();

        if (!existeArchivoPorKey(key)) {
            throw new ArchivoNoEncontradoException("El archivo no existe en S3");
        }

        try {
            ResponseBytes<GetObjectResponse> archivo = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
            return archivo.asByteArray();
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para descargar el archivo", exception);
        } catch (S3Exception exception) {
            throw crearErrorOperacionS3("No fue posible descargar el archivo desde S3", exception);
        }
    }

    public ArchivoResumenResponseDTO eliminarArchivo(Long inscripcionId) {
        eliminarArchivoPorKey(construirKey(inscripcionId));
        return new ArchivoResumenResponseDTO(
                "Archivo eliminado correctamente de S3",
                null,
                null,
                bucketName,
                construirKey(inscripcionId),
                Boolean.FALSE
        );
    }

    public void eliminarArchivoPorKey(String key) {
        validarBucketConfigurado();
        if (!existeArchivoPorKey(key)) {
            throw new ArchivoNoEncontradoException("El archivo no existe en S3");
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para eliminar el archivo", exception);
        } catch (S3Exception exception) {
            throw crearErrorOperacionS3("No fue posible eliminar el archivo desde S3", exception);
        }

    }

    public String construirKey(Long inscripcionId) {
        return "inscripciones/" + inscripcionId + "/resumen-" + inscripcionId + ".txt";
    }

    private boolean existeArchivo(Long inscripcionId) {
        return existeArchivoPorKey(construirKey(inscripcionId));
    }

    public boolean existeArchivoPorKey(String key) {
        validarBucketConfigurado();
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (SdkClientException exception) {
            throw crearErrorConexionS3("No fue posible conectar con AWS S3 para consultar el archivo", exception);
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }

            throw crearErrorOperacionS3("No fue posible consultar el archivo en S3", exception);
        }
    }

    public String construirKeyContenido(Long cursoId, Long contenidoId, String nombreArchivo) {
        return "cursos/" + cursoId + "/contenidos/" + contenidoId + "/" + nombreArchivo;
    }

    public boolean isBucketConfigured() {
        return StringUtils.hasText(bucketName);
    }

    public String getBucketName() {
        return bucketName;
    }

    private void validarBucketConfigurado() {
        if (!StringUtils.hasText(bucketName)) {
            throw new S3StorageException("El bucket S3 no esta configurado", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private S3StorageException crearErrorOperacionS3(String mensaje, AwsServiceException exception) {
        return new S3StorageException(mensaje, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private S3StorageException crearErrorConexionS3(String mensaje, SdkClientException exception) {
        return new S3StorageException(mensaje, HttpStatus.SERVICE_UNAVAILABLE, exception);
    }
}
