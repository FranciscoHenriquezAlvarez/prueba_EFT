# Arquitectura

## Vista general

```text
Postman
  |
  v
AWS API Gateway
  |
  v
ms-cursos-bff :8080
  |-- Oracle
  |-- Amazon EFS
  |-- AWS S3
  |-- RabbitMQ Producer
  |-- RestClient -> ms-eventos-academicos-consumidor :8081
                     |-- RabbitMQ Consumer
                     |-- Oracle
                     |-- Cola de errores
```

## Responsabilidades

- `ms-cursos-bff`
  Recibe las solicitudes HTTP, valida JWT, aplica roles, persiste informacion academica, genera archivos de resumen, usa EFS y S3, publica eventos RabbitMQ y puede solicitar consumo manual del consumidor.

- `ms-eventos-academicos-consumidor`
  Consume mensajes de `eventos-academicos-queue`, valida `mensajeId`, evita duplicados y persiste eventos procesados o con error en Oracle.

## Flujo principal de demostracion

1. PROFESOR o ESTUDIANTE obtiene JWT con Postman.
2. ESTUDIANTE registra una inscripcion.
3. El BFF genera el resumen local, opcionalmente lo sube a S3 y publica `INSCRIPCION_CREADA`.
4. PROFESOR detiene el consumo automatico.
5. PROFESOR ejecuta `POST /api/bff/eventos/consumir?cantidad=1`.
6. El consumidor persiste el mensaje en `EVENTOS_ACADEMICOS_MQ`.

[INSERTAR CAPTURA REAL: diagrama o despliegue de los dos microservicios]
