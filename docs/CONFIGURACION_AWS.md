# Configuracion AWS

## Amazon EFS

Rutas esperadas en EC2:

- `/mnt/efs/resumenes`
- `/mnt/efs/contenidos`

Rutas esperadas dentro del contenedor BFF:

- `/app/efs/resumenes`
- `/app/efs/contenidos`

Variables:

- `RESUMENES_PATH=/app/efs/resumenes`
- `CONTENIDOS_EFS_PATH=/app/efs/contenidos`

## AWS S3

Variable principal:

- `AWS_S3_BUCKET_NAME`

Variables adicionales:

- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`

## Estructura usada en S3

- `inscripciones/{inscripcionId}/resumen-{inscripcionId}.txt`
- `cursos/{cursoId}/contenidos/{contenidoId}/{nombreArchivo}`

## Recomendaciones

- No guardar credenciales reales en el repositorio.
- Validar que el bucket exista antes de la demostracion.
- Confirmar que el montaje EFS este disponible en EC2 antes del despliegue.

[INSERTAR CAPTURA REAL: bucket S3 y montaje EFS]
