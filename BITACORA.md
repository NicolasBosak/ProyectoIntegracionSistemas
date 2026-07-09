# Bitácora de trabajo — CampusConnect 360

> Entregable obligatorio (sección 14.3 de la consigna). Registro de integrantes,
> responsabilidades, decisiones, problemas y uso de IA.

## Integrantes y responsabilidades

> ⚠️ Completar con los datos reales del grupo. La distribución respalda la autoría individual;
> cada integrante debe poder **explicar y defender** su parte.

| Integrante | Rol / servicio principal | Responsabilidades |
|---|---|---|
| _(nombre)_ | Servicio Académico + API Gateway | Estudiantes, matrículas, seguridad JWT |
| _(nombre)_ | Servicio de Pagos + Analítica | Pagos, read model, dashboard |
| _(nombre)_ | Asistencia/Bienestar + Notificaciones | Asistencia, incidentes, notificaciones, DLQ |
| _(nombre)_ | Frontend (portales + dashboard) | Interfaces React, integración con gateway |

> Sugerencia: aunque haya un responsable principal por área, roten en revisiones (PRs) para
> que todos comprendan el conjunto.

## Repositorio Git

- Enlace: _(pendiente)_

## Registro de decisiones y avance

### Paso 1 — Arquitectura, dominio y contratos
- **Decisiones de stack:** Spring Boot, RabbitMQ, React, Spring Cloud Gateway, PostgreSQL, Docker Compose.
- **Justificación de RabbitMQ sobre Kafka:** DLQ, reintentos e idempotencia más simples de
  evidenciar; menor peso en Docker para el entorno de demostración.
- **CQRS ligero:** `analytics_db` como read model que alimenta el dashboard.
- Entregables: documentos en `docs/` (arquitectura, contratos de eventos y de API, mensajería).

### Paso 2 — Repositorio base e infraestructura
- Estructura del repo por servicios + `frontend/` + `docs/`.
- `docker-compose.yml` con RabbitMQ (panel 15672), PostgreSQL y Adminer.
- Una base de datos por servicio creada vía `infra/postgres/init/`.
- Convenciones de ramas y commits definidas ([CONVENCIONES.md](CONVENCIONES.md)).
- **Decisión:** una sola instancia PostgreSQL con 5 bases separadas (aislamiento lógico,
  laptop-friendly); en producción serían instancias independientes.

## Problemas encontrados
- _(registrar aquí a medida que aparezcan)_

## Herramientas utilizadas
- Docker / Docker Compose, RabbitMQ, PostgreSQL, Adminer, Git.
- _(agregar IDE, librerías, etc.)_

## Uso de IA generativa
- Apoyo en la generación del plan de trabajo, andamiaje de documentación de arquitectura y
  contratos. El grupo revisa, adapta y valida todo el contenido.
- _(registrar cada uso posterior: para qué se usó y qué se adaptó)_
