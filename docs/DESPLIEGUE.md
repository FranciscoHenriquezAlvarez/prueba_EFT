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

## Docker EC2

```bash
docker compose -f docker-compose.ec2.yml config
docker compose -f docker-compose.ec2.yml pull
docker compose -f docker-compose.ec2.yml up -d --remove-orphans
docker compose -f docker-compose.ec2.yml ps
curl --fail http://localhost:8080/api/health
curl --fail http://localhost:8081/api/health
```

## Workflow

Archivo: `.github/workflows/deploy.yml`

Nombre esperado:

- `Deploy EFT Semana 09`

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

[INSERTAR CAPTURA REAL: ejecucion del workflow y health checks]
