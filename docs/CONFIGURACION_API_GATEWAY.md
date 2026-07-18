# Configuracion API Gateway

La configuracion se mantiene manual, sin rutas `ANY`, y fue revisada contra los controladores reales del repositorio.

## Resumen validado

- Total de endpoints en `ms-cursos-bff`: `34`
- Total de endpoints en `ms-eventos-academicos-consumidor`: `6`
- Total revisado en codigo: `40`

## Placeholders

- API ID: `REEMPLAZAR_API_ID`
- Stage: `REEMPLAZAR_STAGE`
- URL base: `https://REEMPLAZAR.execute-api.REEMPLAZAR_REGION.amazonaws.com`
- VPC Link o destino privado: `REEMPLAZAR_SI_APLICA`

## Integraciones hacia `ms-cursos-bff`

| Metodo | Path | Destino | Puerto | JWT | Rol esperado |
|---|---|---|---|---|---|
| GET | `/api/health` | `ms-cursos-bff` | `8080` | No | Publico |
| GET | `/api/cursos` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE |
| POST | `/api/cursos` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/cursos/{id}` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE |
| PUT | `/api/cursos/{id}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| DELETE | `/api/cursos/{id}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/estudiantes` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/estudiantes` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/estudiantes/{id}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| PUT | `/api/estudiantes/{id}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/inscripciones` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/inscripciones/{id}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/inscripciones` | `ms-cursos-bff` | `8080` | Si | ESTUDIANTE |
| POST | `/api/inscripciones/{inscripcionId}/enviar-mq` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/inscripciones/{inscripcionId}/generar-archivo` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/inscripciones/{inscripcionId}/subir-s3` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/inscripciones/{inscripcionId}/consultar-s3` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/inscripciones/{inscripcionId}/descargar-s3` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/cursos/{cursoId}/contenidos` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/cursos/{cursoId}/contenidos` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE inscrito |
| GET | `/api/contenidos/{contenidoId}` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE inscrito |
| GET | `/api/contenidos/{contenidoId}/descargar` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE inscrito |
| DELETE | `/api/contenidos/{contenidoId}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/cursos/{cursoId}/examenes` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/cursos/{cursoId}/examenes` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE |
| GET | `/api/examenes/{examenId}` | `ms-cursos-bff` | `8080` | Si | PROFESOR o ESTUDIANTE |
| PUT | `/api/examenes/{examenId}` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/examenes/{examenId}/intentos` | `ms-cursos-bff` | `8080` | Si | ESTUDIANTE inscrito |
| GET | `/api/examenes/{examenId}/intentos` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/estudiantes/{estudianteId}/intentos` | `ms-cursos-bff` | `8080` | Si | PROFESOR o dueño del recurso |
| PUT | `/api/intentos/{intentoId}/calificacion` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| GET | `/api/estudiantes/{estudianteId}/calificaciones` | `ms-cursos-bff` | `8080` | Si | PROFESOR o dueño del recurso |
| GET | `/api/cursos/{cursoId}/calificaciones` | `ms-cursos-bff` | `8080` | Si | PROFESOR |
| POST | `/api/bff/eventos/consumir` | `ms-cursos-bff` | `8080` | Si | PROFESOR |

## Integraciones hacia `ms-eventos-academicos-consumidor`

| Metodo | Path | Destino | Puerto | JWT | Rol esperado |
|---|---|---|---|---|---|
| GET | `/api/health` | `ms-eventos-academicos-consumidor` | `8081` | No | Publico tecnico |
| GET | `/api/mq/procesados` | `ms-eventos-academicos-consumidor` | `8081` | Si | PROFESOR |
| GET | `/api/mq/errores` | `ms-eventos-academicos-consumidor` | `8081` | Si | PROFESOR |
| GET | `/api/mq/modo-consumo` | `ms-eventos-academicos-consumidor` | `8081` | Si | PROFESOR |
| PUT | `/api/mq/modo-consumo` | `ms-eventos-academicos-consumidor` | `8081` | Si | PROFESOR |
| POST | `/api/mq/consumir` | `ms-eventos-academicos-consumidor` | `8081` | Si | PROFESOR |

## Notas de configuracion

- Crear una ruta por cada metodo y path exacto.
- No reutilizar un recurso `/{proxy+}` ni `ANY`.
- Asociar authorizer JWT a todas las rutas marcadas con `Si`.
- Mantener `GET /api/health` sin JWT para smoke test tecnico.
- Si el consumidor se expone solo de forma privada, preferir integracion interna y dejar el acceso externo limitado al BFF.

## Pruebas Postman sugeridas

- `GET {{api_gateway_url}}/api/health`
- `POST {{api_gateway_url}}/api/inscripciones`
- `POST {{api_gateway_url}}/api/cursos/1/contenidos`
- `POST {{api_gateway_url}}/api/cursos/1/examenes`
- `POST {{api_gateway_url}}/api/examenes/1/intentos`
- `PUT {{api_gateway_url}}/api/intentos/1/calificacion`
- `POST {{api_gateway_url}}/api/bff/eventos/consumir?cantidad=1`
- `GET {{api_gateway_url}}/api/mq/procesados`

[INSERTAR CAPTURA REAL: rutas configuradas en API Gateway]
