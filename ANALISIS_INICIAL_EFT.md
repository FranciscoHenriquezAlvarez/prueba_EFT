# ANALISIS INICIAL EFT

## 1. Verificacion de rutas

- Ruta Semana 08 verificada: `/home/francisco/proyecto/DESARROLLO CLOUD NATIVE/SEMANA 08/cloudnativeapp_semana08`
- Ruta Semana 07 verificada: `/home/francisco/proyecto/DESARROLLO CLOUD NATIVE/SEMANA 07/cloudnativeapp`
- Ruta EFT verificada: `/home/francisco/proyecto/DESARROLLO CLOUD NATIVE/EFT/cloudnativeapp_EFT`

## 2. Estado inicial detectado en EFT

- La carpeta `cloudnativeapp_EFT` no contenia una base funcional del proyecto.
- Solo existian los directorios reservados `.agents`, `.codex` y `.git`.
- Para cumplir la pauta, se copio Semana 08 como base tecnica hacia EFT.
- La copia excluyo `.git`, `target`, `.env`, `.idea`, `.vscode`, `wallets`, `logs`, `*.class`, `*.jar` y archivos `Zone.Identifier`.

## 3. Estructura detectada en Semana 08

Semana 08 aporta la base fisica y tecnica principal:

- Proyecto Maven multi-modulo.
- `pom.xml` padre con modulos:
  - `ms-guias-productor`
  - `ms-guias-consumidor`
- Microservicio productor con:
  - Spring Boot
  - Spring Data JPA
  - Spring Security OAuth2 Resource Server
  - RabbitMQ
  - AWS S3
  - escritura temporal en EFS
  - Oracle JDBC y Wallet
  - Dockerfile propio
  - pruebas de seguridad e integracion
- Microservicio consumidor con:
  - `@RabbitListener`
  - consumo manual con `RabbitTemplate.receiveAndConvert`
  - persistencia Oracle
  - cola de errores
  - control del modo de consumo
  - Dockerfile propio
  - pruebas de seguridad, servicio e integracion
- Infraestructura operativa:
  - `docker-compose.ec2.yml`
  - `.github/workflows/deploy.yml`
  - `README.md`
  - Maven Wrapper

## 4. Estructura detectada en Semana 07

Semana 07 aporta el dominio academico y servicios reutilizables:

- Entidades:
  - `Curso`
  - `Estudiante`
  - `Inscripcion`
  - `DetalleInscripcion`
  - `ResumenInscripcionMq`
- DTO:
  - `CursoRequestDTO`
  - `CursoResponseDTO`
  - `EstudianteRequestDTO`
  - `EstudianteResponseDTO`
  - `InscripcionRequestDTO`
  - `InscripcionResumenDTO`
  - `DetalleInscripcionResumenDTO`
  - `ArchivoResumenResponseDTO`
  - `ResumenInscripcionMensaje`
- Repositories:
  - `CursoRepository`
  - `EstudianteRepository`
  - `InscripcionRepository`
  - `DetalleInscripcionRepository`
  - `ResumenInscripcionMqRepository`
- Services:
  - `CursoService`
  - `EstudianteService`
  - `InscripcionService`
  - `ResumenArchivoService`
  - `S3StorageService`
  - `ResumenInscripcionMqService`
- Controllers:
  - `CursoController`
  - `EstudianteController`
  - `InscripcionController`
  - `HealthController`
- Integraciones utiles:
  - generacion de resumen de inscripcion en texto
  - almacenamiento temporal local/EFS
  - almacenamiento permanente en S3
  - publicacion y consumo simple en RabbitMQ
  - seguridad JWT con claim `extension_consultaRole`
  - pruebas automatizadas con H2

## 5. Funcionalidades reutilizables

### Desde Semana 08

- Estructura multi-modulo Maven.
- Separacion en productor y consumidor.
- `SecurityConfig` mas completo para soportar claims con uno o varios roles.
- `RabbitMQConfig` con exchange, colas durables y mensajes persistentes.
- `JacksonConfig` del flujo RabbitMQ.
- `MqEvidenceController` y el servicio de consumo manual/automatico.
- configuracion Oracle Wallet.
- Dockerfiles.
- `docker-compose.ec2.yml`.
- workflow de despliegue en GitHub Actions.
- health checks y estrategia de despliegue EC2.

### Desde Semana 07

- Modelo academico de cursos, estudiantes e inscripciones.
- calculo del total de una inscripcion.
- resumen de inscripcion en archivo `.txt`.
- `S3StorageService` con manejo de errores de S3.
- endpoints academicos base.
- excepciones y DTO del dominio.

## 6. Clases que se migraran desde Semana 07

Migracion directa o adaptada hacia `ms-cursos-bff`:

- `Curso`
- `Estudiante`
- `Inscripcion`
- `DetalleInscripcion`
- `CursoRepository`
- `EstudianteRepository`
- `InscripcionRepository`
- `DetalleInscripcionRepository`
- `CursoService`
- `EstudianteService`
- `InscripcionService`
- `ResumenArchivoService`
- `S3StorageService`
- `CursoController`
- `EstudianteController`
- DTO de cursos, estudiantes e inscripciones
- excepciones del dominio academico

Migracion parcial o inspirada:

- `ResumenInscripcionMensaje` como base para `EventoAcademicoMensaje`
- `ResumenInscripcionMqService` como referencia para publicar eventos academicos

## 7. Configuraciones que se conservaran desde Semana 08

- `pom.xml` padre multi-modulo.
- dependencias base de Spring Boot, Security, RabbitMQ, Oracle y AWS SDK.
- `SecurityConfig` de ambos microservicios como base de autorizacion.
- `RabbitMQConfig` y convertidor JSON.
- consumo automatico y manual del consumidor.
- persistencia separada de mensajes procesados y con error.
- puertos:
  - BFF: `8080`
  - consumidor: `8081`
- Dockerfiles y compose para EC2.
- workflow `.github/workflows/deploy.yml`.
- variables de Wallet Oracle.
- estrategia H2 para pruebas.

## 8. Clases que deberan adaptarse

- `GuiasProductorApplication` -> clase principal del futuro `ms-cursos-bff`.
- `GuiasConsumidorApplication` -> clase principal del futuro `ms-eventos-academicos-consumidor`.
- `SecurityRoles` en ambos modulos:
  - reemplazar `usuario` y `administrador`
  - usar `PROFESOR` y `ESTUDIANTE`
- `SecurityConfig`:
  - actualizar endpoints permitidos por rol
  - mantener claim `extension_consultaRole`
- `RabbitMQConfig`:
  - exchange `eventos-academicos-exchange`
  - cola `eventos-academicos-queue`
  - cola de error `eventos-academicos-error-queue`
  - routing keys academicas
- `application.properties`:
  - nombres de microservicios
  - variables `RESUMENES_PATH`, `CONTENIDOS_EFS_PATH`, `EVENTOS_CONSUMIDOR_URL`
- `docker-compose.ec2.yml`:
  - nombres de servicios e imagenes finales
  - volumen EFS para resumenes y contenidos
- `deploy.yml`:
  - nombre del workflow
  - nombres de imagenes Docker Hub
  - ruta remota EFT

## 9. Clases nuevas requeridas

En `ms-cursos-bff`:

- `BffOrchestrationService`
- `ContenidoCurso`
- `ContenidoCursoRepository`
- `ContenidoCursoService`
- `ContenidoCursoController`
- `Examen`
- `ExamenRepository`
- `ExamenService`
- `ExamenController`
- `IntentoExamen`
- `IntentoExamenRepository`
- `IntentoExamenService`
- `IntentoExamenController`
- `EventoAcademicoMensaje`
- `EventoAcademicoPublisherService`
- cliente `RestClient` para llamar al consumidor
- endpoint `POST /api/bff/eventos/consumir`

En `ms-eventos-academicos-consumidor`:

- entidad de persistencia academica, basada en una cola academica previa, por ejemplo `EventoAcademicoMq`
- repository correspondiente
- DTO de respuesta academica del consumidor
- adaptacion del listener y del servicio de consumo al nuevo contrato de mensaje

Fuera de los modulos:

- `database/01_create_tables.sql`
- `database/02_initial_data.sql`
- `database/03_test_queries.sql`
- `database/README.md`
- `docs/CONFIGURACION_API_GATEWAY.md`
- `docs/PRUEBAS_POSTMAN.md`
- coleccion y ambientes Postman

## 10. Dependencias existentes relevantes

- Java 17.
- Spring Boot 4.0.6.
- Spring Web.
- Spring Data JPA.
- Spring Validation.
- Spring Security.
- OAuth2 Resource Server.
- Spring AMQP.
- AWS SDK S3 `2.31.77`.
- Oracle JDBC `21.9.0.0`.
- Oracle Wallet libraries:
  - `oraclepki`
  - `osdt_core`
  - `osdt_cert`
- H2 para pruebas.
- Spring Boot Test.
- Spring Security Test.

## 11. Riesgos tecnicos detectados

- El proyecto objetivo partio sin base funcional; cualquier desarrollo requiere construir primero sobre la copia controlada de Semana 08.
- Semana 07 usa un proyecto monolitico con un solo modulo, por lo que la migracion al BFF exigira adaptar paquetes, entidades y pruebas.
- Semana 07 usa nombres y campos minimos academicos; la pauta EFT exige campos adicionales como `activo`, `fechaCreacion`, `identificadorIdaas` y `estado`.
- Semana 08 esta montado sobre el dominio de guias de despacho; hay que renombrar modulos, paquetes y contratos sin romper la logica tecnica existente.
- El flujo de seguridad del BFF necesitara controles adicionales para que un `ESTUDIANTE` solo consulte sus propios datos y solo acceda a contenido de cursos inscritos.
- La configuracion productiva de Oracle, Azure AD B2C, AWS S3 y API Gateway no puede validarse completamente sin valores reales, por lo que deberan mantenerse placeholders claros.
- El build inicial pasa con H2, pero las nuevas entidades y endpoints exigiran ampliar pruebas para evitar regresiones.

## 12. Plan de trabajo por fases

### Fase 1. Auditoria y base

- Verificar rutas.
- Copiar Semana 08 a EFT con exclusiones.
- Ejecutar build inicial.
- Registrar hallazgos en este documento.

### Fase 2. Renombramiento estructural

- Renombrar modulos:
  - `ms-guias-productor` -> `ms-cursos-bff`
  - `ms-guias-consumidor` -> `ms-eventos-academicos-consumidor`
- Ajustar `pom.xml`, artifactId, nombres de aplicacion, paquetes y Dockerfiles.

### Fase 3. Migracion del dominio academico al BFF

- Migrar entidades, DTO, repositories, services y controllers de cursos, estudiantes e inscripciones.
- Crear `BffOrchestrationService`.
- Integrar generacion de resumen en EFS y subida a S3.
- Publicar `INSCRIPCION_CREADA` en RabbitMQ.

### Fase 4. Funcionalidades nuevas minimas

- Implementar `ContenidoCurso`.
- Implementar `Examen`.
- Implementar `IntentoExamen` con calificacion simple.
- Agregar consultas de calificaciones.

### Fase 5. Consumidor academico

- Adaptar el contrato de mensaje.
- Mantener consumo automatico y manual.
- Persistir eventos procesados y errores en Oracle.

### Fase 6. Infraestructura y documentacion tecnica

- Actualizar compose y workflow.
- Crear scripts `database/`.
- Crear documentos `docs/`.
- Crear coleccion y ambientes Postman.

### Fase 7. Validacion final

- Ejecutar `./mvnw clean test package`.
- Revisar pruebas por modulo.
- Validar compose con `docker compose config`.

## 13. Resultado del build inicial

Comando ejecutado:

```bash
./mvnw clean test package
```

Resultado:

- `BUILD SUCCESS`
- tiempo total aproximado: `35.888 s`
- modulos validados:
  - `cloudnativeapp-semana08`
  - `ms-guias-productor`
  - `ms-guias-consumidor`
- pruebas ejecutadas: `15`
- fallos: `0`
- errores: `0`
- omitidas: `0`

Observaciones:

- La base copiada desde Semana 08 compila correctamente en EFT antes de iniciar la adaptacion academica.
- El resultado confirma que la copia realizada no rompio la estructura multi-modulo.
- A partir de este punto ya es seguro iniciar el renombramiento y la migracion del dominio.
