# Despliegue

## Build local

```bash
./mvnw clean test package
```

## Docker local

```bash
docker compose config
docker compose up -d
```

`docker-compose.yml` se utiliza para la ejecucion local.

## Docker EC2

```bash
docker compose -f docker-compose.ec2.yml config
docker compose -f docker-compose.ec2.yml pull
docker compose -f docker-compose.ec2.yml up -d --remove-orphans
docker compose -f docker-compose.ec2.yml ps
curl --fail http://localhost:8080/api/health
curl --fail http://localhost:8081/api/health
```

`docker-compose.ec2.yml` se utiliza como fuente  del despliegue en EC2. El consumidor espera que RabbitMQ se encuentre saludable y el BFF depende de RabbitMQ y del consumidor.

## Workflow

Archivo: `.github/workflows/deploy.yml`

Nombre esperado:

- `Deploy EFT`

El pipeline copia `docker-compose.ec2.yml` desde el repositorio hacia EC2, genera solo el archivo `.env` remoto y luego ejecuta `pull`, `up`, `ps` y los health checks finales.

## GitHub Secrets

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `EC2_HOST`
- `EC2_USER`
- `EC2_SSH_KEY`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `AWS_S3_BUCKET_NAME`
- `JWT_ISSUER_URI`
- `JWT_AUDIENCE`
- `JWT_JWK_SET_URI`
- `RABBITMQ_USER`
- `RABBITMQ_PASS`


