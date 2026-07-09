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

### Paso 3 — Servicio Académico
- Microservicio Spring Boot (Java 21): entidades `Student`/`Enrollment`/`EventLog`, API REST
  `/academic/*`, Swagger/OpenAPI, health check (Actuator).
- **Publicación de eventos:** `EventPublisher` + `EventEnvelope` (envelope estándar) envían
  `StudentEnrolled` al topic exchange `campus.events` con routing key `student.enrolled`.
- **Codificación de IDs de negocio** (`STU-001`, `ENR-001`) asignados tras persistir.
- `EventLog` local para el historial de eventos por estudiante (trazabilidad).
- Datos semilla: 3 estudiantes de ejemplo (sin publicar eventos, representan datos previos).
- **Decisión:** el servicio Académico es *productor*; las colas/bindings los declara cada
  consumidor (Notificaciones/Analítica en pasos 4+). Aquí solo se declara el exchange.

### Paso 4 — Notificaciones + Analítica (Publish/Subscribe)
- **notification-service:** consume `StudentEnrolled`/`PaymentConfirmed`/`IncidentReported`
  (colas `q.notifications.*` con Dead Letter Channel hacia `campus.dlx`), genera notificación
  simulada y publica `NotificationSent`. Reintentos + `default-requeue-rejected: false`.
- **analytics-service:** cola `q.analytics.all` con binding `#` (recibe todos los eventos),
  read model CQRS en `metric_counters` + `event_records`; expone `/analytics/dashboard` y
  `/analytics/events`. Guardia de idempotencia por `eventId`.
- **Problema resuelto (mensajería entre servicios):** el productor añade el header
  `__TypeId__` con su clase; el consumidor, en otro paquete, no la tiene → `ClassNotFoundException`.
  **Solución:** conversor `Jackson2JsonMessageConverter` con `DefaultJackson2JavaTypeMapper`
  en `TypePrecedence.INFERRED` y `trustedPackages("*")`, deserializando según el tipo del
  `@RabbitListener`. Mantiene los servicios desacoplados sin compartir clases.
- **Nota CQRS:** analítica es event-sourced; solo cuenta eventos ocurridos mientras está
  activa (los 3 estudiantes semilla, que no publican eventos, no se contabilizan).

## Problemas encontrados
- _(registrar aquí a medida que aparezcan)_

## Herramientas utilizadas
- Docker / Docker Compose, RabbitMQ, PostgreSQL, Adminer, Git.
- _(agregar IDE, librerías, etc.)_

## Uso de IA generativa
- Apoyo en la generación del plan de trabajo, andamiaje de documentación de arquitectura y
  contratos. El grupo revisa, adapta y valida todo el contenido.
- _(registrar cada uso posterior: para qué se usó y qué se adaptó)_
