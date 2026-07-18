# Pruebas Postman

## Ambientes

- `CloudNative_EFT_Local.postman_environment.json`
- `CloudNative_EFT_AWS.postman_environment.json`

## Variables

- `base_url_bff`
- `base_url_consumer`
- `api_gateway_url`
- `access_token_profesor`
- `access_token_estudiante`
- `curso_id`
- `estudiante_id`
- `inscripcion_id`
- `contenido_id`
- `examen_id`
- `intento_id`

## Flujo sugerido

1. Obtener JWT de `PROFESOR`.
2. Obtener JWT de `ESTUDIANTE`.
3. Crear cursos con `PROFESOR`.
4. Crear estudiantes con `PROFESOR`.
5. Registrar inscripcion con `ESTUDIANTE`.
6. Subir contenido del curso con `PROFESOR`.
7. Crear examen con `PROFESOR`.
8. Registrar intento con `ESTUDIANTE`.
9. Calificar con `PROFESOR`.
10. Publicar evento y consumirlo manualmente.

## Pruebas de seguridad recomendadas

- `GET /api/health` sin token devuelve `200`
- Endpoint protegido sin token devuelve `401`
- `ESTUDIANTE` intentando `POST /api/cursos` devuelve `403`
- `PROFESOR` creando curso devuelve `201`
- `ESTUDIANTE` descargando contenido inscrito devuelve `200`

## Autenticacion OAuth2

Configurar en Postman con placeholders reales del tenant Azure AD B2C:

- Authorization URL: `REEMPLAZAR`
- Access Token URL: `REEMPLAZAR`
- Client ID: `REEMPLAZAR`
- Scope: `REEMPLAZAR`

[INSERTAR CAPTURA REAL: solicitud de token JWT en Postman]
