# Configuracion IDAAS

La solucion mantiene Azure AD B2C como proveedor de autenticacion.

## Variables usadas

- `JWT_ISSUER_URI`
- `JWT_AUDIENCE`
- `JWT_JWK_SET_URI`

## Claim de roles

- Claim esperado: `extension_consultaRole`
- Roles utilizados:
  - `PROFESOR`
  - `ESTUDIANTE`

## Relacion entre JWT y estudiante

La entidad `ESTUDIANTES` contiene `IDENTIFICADOR_IDAAS`.

El BFF intenta relacionar el JWT con el estudiante usando:

1. `sub`
2. `email`
3. `emails[0]`

## Prueba recomendada

1. Obtener token de `PROFESOR` en Postman.
2. Obtener token de `ESTUDIANTE` en Postman.
3. Guardar los valores en:
   - `access_token_profesor`
   - `access_token_estudiante`

[INSERTAR CAPTURA REAL: configuracion OAuth2 en Postman]
