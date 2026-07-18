# Configuracion RabbitMQ

## Elementos

- Exchange: `eventos-academicos-exchange`
- Cola principal: `eventos-academicos-queue`
- Cola de errores: `eventos-academicos-error-queue`
- Routing key principal: `evento.academico`
- Routing key error: `evento.academico.error`

## Variables

- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USER`
- `RABBITMQ_PASS`

## Eventos

- `INSCRIPCION_CREADA`
- `EXAMEN_REALIZADO`
- `CALIFICACION_REGISTRADA`

## Flujo de prueba

1. Detener consumo automatico:
   `PUT /api/mq/modo-consumo?automatico=false`
2. Publicar evento desde el BFF:
   `POST /api/inscripciones/{id}/enviar-mq`
3. Verificar mensaje `Ready` en RabbitMQ Management.
4. Consumir manualmente:
   `POST /api/bff/eventos/consumir?cantidad=1`
5. Consultar procesados:
   `GET /api/mq/procesados`

[INSERTAR CAPTURA REAL: exchange, cola principal y cola de errores]
