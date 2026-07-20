# Scripts Oracle

Scripts resumen estructura utilizada por el proyecto EFT.

## Archivos

- `01_create_tables.sql`
  Crea las tablas principales usadas por los dos microservicios.

- `02_initial_data.sql`
  Inserta cursos y estudiantes base para pruebas manuales.

- `03_test_queries.sql`
  Incluye consultas simples para validar persistencia academica y eventos MQ.

## Consideraciones

- No se incluyen `DB_URL`, usuarios, claves ni contenido del Oracle Wallet.
- Las tablas estan alineadas con las entidades JPA actuales del proyecto.
- En produccion puede mantenerse `spring.jpa.hibernate.ddl-auto=update`, pero estos scripts ayudan a documentar la estructura esperada.
