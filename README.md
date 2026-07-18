# Cloud Native EFT

Plataforma Cloud Native academica para gestion de cursos en linea construida con Java 17, Spring Boot, Spring Security, RabbitMQ, Oracle, Amazon EFS, AWS S3 y despliegue automatizado en EC2.

## Microservicios

- `ms-cursos-bff`
  Punto de entrada principal. Gestiona cursos, estudiantes, inscripciones, contenidos, examenes e intentos. Integra Oracle, EFS, S3, RabbitMQ y orquesta el consumo manual del microservicio consumidor.

- `ms-eventos-academicos-consumidor`
  Consume eventos academicos desde RabbitMQ, persiste mensajes procesados en Oracle y registra errores controlados.

## Endpoints principales

- `GET /api/health`
- `GET|POST|PUT|DELETE /api/cursos`
- `GET|POST|PUT /api/estudiantes`
- `GET|POST /api/inscripciones`
- `POST /api/inscripciones/{id}/enviar-mq`
- `POST /api/cursos/{cursoId}/contenidos`
- `POST /api/cursos/{cursoId}/examenes`
- `POST /api/examenes/{examenId}/intentos`
- `PUT /api/intentos/{intentoId}/calificacion`
- `POST /api/bff/eventos/consumir`
- `GET|PUT|POST /api/mq/*`

## Variables relevantes

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `ORACLE_WALLET_PATH`
- `JWT_ISSUER_URI`
- `JWT_AUDIENCE`
- `JWT_JWK_SET_URI`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USER`
- `RABBITMQ_PASS`
- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `AWS_S3_BUCKET_NAME`
- `RESUMENES_PATH`
- `CONTENIDOS_EFS_PATH`
- `EVENTOS_CONSUMIDOR_URL`

## Ejecucion local

```bash
./mvnw clean test package
docker compose config
docker compose up -d
```

## Estructura de entrega

- `database/`
- `docs/`
- `postman/`
- `docker-compose.yml`
- `docker-compose.ec2.yml`
- `.github/workflows/deploy.yml`

## Notas

- No se almacenan secretos reales en el repositorio.
- La configuracion de API Gateway, Azure AD B2C y AWS debe completarse con valores reales del entorno.
- La demostracion academica recomendada usa Postman como cliente principal.
