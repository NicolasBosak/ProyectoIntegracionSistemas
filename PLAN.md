# Plan de Trabajo — CampusConnect 360

> Ecosistema funcional de integración para una red de colegios
> Proyecto Integrador Final · Integración de Sistemas · 6 semanas · Trabajo grupal
> Evaluación: presentación funcional, demo en vivo y defensa técnica (semana 16)

El plan sigue la recomendación de la consigna: **construir primero los flujos principales
funcionando de punta a punta y luego fortalecer** seguridad, resiliencia, dashboard,
observabilidad y calidad visual. Cada paso deja evidencia (código + commits + documentación)
para la cláusula de autoría y la defensa individual.

---

## Paso 1 — Definir arquitectura, dominio y contratos (Semana 1) ✅

> **Completado.** Entregables en [`docs/`](docs/README.md): arquitectura, contratos de
> eventos, contratos de API y topología RabbitMQ. Stack fijado: Spring Boot · RabbitMQ ·
> React · Spring Cloud Gateway · PostgreSQL · Docker Compose.

**Objetivo:** acordar la solución antes de escribir servicios, para no rehacer trabajo.

- Modelar el dominio: Estudiante, Matrícula, Pago, Asistencia, Incidente, Notificación.
- Definir los **microservicios** (responsabilidad única cada uno):
  Académico, Pagos, Notificaciones, Asistencia/Bienestar, Analítica + **API Gateway**.
- Diseñar los **contratos de eventos** (mínimo 4): `StudentEnrolled`, `PaymentConfirmed`,
  `AttendanceRecorded`, `IncidentReported` (+ opcionales `NotificationSent/Failed`,
  `StudentStatusUpdated`). Cada evento con: `eventId`, `eventType`, `occurredAt`,
  `correlationId`, id de entidad y datos relevantes.
- Elegir el **stack** (frontend, backend, RabbitMQ o Kafka, gateway, BD, dashboard) y dejarlo
  registrado en la bitácora con la justificación técnica.
- Definir el esquema de **canales/colas** (Message Channel) y qué patrón usa cada uno
  (Pub/Sub vs Point-to-Point).

**Entregable:** diagrama de arquitectura + diagrama de flujo de eventos + tabla de contratos.

---

## Paso 2 — Base del repositorio y esqueleto Docker Compose (Semana 1) ✅

> **Completado.** Estructura del repo por servicios + `frontend/` + `docs/`;
> [`docker-compose.yml`](docker-compose.yml) (RabbitMQ + PostgreSQL + Adminer, sintaxis
> validada), [`infra/postgres/init/`](infra/postgres/init/01-create-databases.sql) crea las
> 5 bases, [`.env.example`](.env.example), [`README.md`](README.md),
> [`CONVENCIONES.md`](CONVENCIONES.md) y [`BITACORA.md`](BITACORA.md).
> Pendiente: ejecutar `docker compose up -d` con Docker Desktop encendido.

**Objetivo:** que "todo levante" desde el primer día, aunque los servicios estén vacíos.

- Crear el repositorio Git con estructura por servicios (carpeta por microservicio + frontend).
- `docker-compose.yml` con: broker (RabbitMQ/Kafka), bases de datos **separadas por servicio**,
  y contenedores placeholder de cada servicio.
- `README.md` inicial, archivos `.env.example`, y convención de commits.
- Definir asignación de **responsabilidades por integrante** (queda como evidencia de autoría).

**Entregable:** `docker compose up` levanta broker + BDs sin errores.

---

## Paso 3 — Servicio Académico + evento StudentEnrolled (Semana 2) ✅

> **Completado (backend).** Microservicio Spring Boot en
> [`services/academic-service`](services/academic-service/README.md): API REST `/academic/*`,
> persistencia en `academic_db`, publicación de `StudentEnrolled` a `campus.events`
> (routing key `student.enrolled`), historial de eventos, datos semilla y Swagger/OpenAPI.
> Habilitado en [`docker-compose.yml`](docker-compose.yml). Pendiente de ejecutar con Docker.

**Objetivo:** primer flujo vertical completo (UI → API → BD → evento → consumidor).

- API REST del servicio Académico: registrar estudiante, crear matrícula, consultar ficha.
- Persistencia en su propia base de datos.
- Publicar el evento `StudentEnrolled` al broker al matricular.
- Documentar la API con **Swagger/OpenAPI**.

**Entregable:** matricular un estudiante publica correctamente el evento en el broker.

---

## Paso 4 — Servicio de Notificaciones + Analítica base (Publish/Subscribe) (Semana 2) ✅

> **Completado (backend).** Dos microservicios consumidores:
> [`notification-service`](services/notification-service/README.md) (colas
> `q.notifications.*` con DLQ, publica `NotificationSent/Failed`) y
> [`analytics-service`](services/analytics-service/README.md) (cola `q.analytics.all`
> con binding `#`, read model CQRS, `/analytics/dashboard`). Conversor JSON en modo
> INFERRED para desacoplar productor/consumidor. Ambos habilitados en Compose.

**Objetivo:** demostrar que **varios consumidores** reaccionan a un mismo evento.

- Servicio de Notificaciones: consume `StudentEnrolled`, registra una notificación *simulada*
  y emite `NotificationSent` / `NotificationFailed`.
- Servicio de Analítica: consume el mismo evento y actualiza indicadores (vista consolidada / CQRS).
- Dejar clara la evidencia de **Pub/Sub** (mismo evento, múltiples consumidores) y
  **Point-to-Point** donde aplique.

**Entregable:** un `StudentEnrolled` impacta a la vez notificaciones y analítica.

---

## Paso 5 — Servicio de Pagos + flujo PaymentConfirmed (Semana 3) ✅

> **Completado (backend).** [`payment-service`](services/payment-service/README.md): API
> `/payments/*`, consume `StudentEnrolled` (crea deuda de matrícula + proyección local),
> publica `PaymentConfirmed`. [`academic-service`](services/academic-service/README.md)
> ahora **consume** `PaymentConfirmed` (cola `q.academic.payment`) con **idempotencia**
> (`processed_events`), actualiza el estado financiero y publica `StudentStatusUpdated`.
> El `correlationId` de la matrícula se conserva a lo largo de todo el flujo.

**Objetivo:** segundo flujo de negocio y actualización cruzada entre servicios.

- API del servicio de Pagos: consultar estudiantes/deudas, registrar obligación, confirmar pago.
- Publicar `PaymentConfirmed`.
- El servicio Académico consume el evento y **actualiza el estado financiero** del estudiante
  (posible `StudentStatusUpdated`); Notificaciones y Analítica reaccionan.
- Swagger/OpenAPI del servicio de Pagos.

**Entregable:** confirmar un pago se refleja en académico, notificaciones y analítica.

---

## Paso 6 — Servicio de Asistencia/Bienestar (Semana 3) ✅

> **Completado (backend).** [`attendance-service`](services/attendance-service/README.md):
> API `/attendance/*` (asistencia, incidentes, historial), consume `StudentEnrolled`
> (proyección local), publica `AttendanceRecorded` e `IncidentReported`. Con esto están los
> **4 eventos de negocio** obligatorios. Habilitado en Compose (7 servicios).

**Objetivo:** completar los 4 eventos de negocio mínimos.

- API para registrar asistencia, ausencia e incidente/novedad.
- Publicar `AttendanceRecorded` e `IncidentReported`.
- Notificaciones genera alerta simulada cuando aplique; Analítica actualiza indicadores.
- Swagger/OpenAPI del servicio.

**Entregable:** los 4 eventos obligatorios funcionando de punta a punta.

---

## Paso 7 — Frontends funcionales + Dashboard directivo (Semana 4) ✅

> **Completado.** SPA React + Vite en [`frontend/`](frontend/README.md): Portal Académico,
> Financiero, Docente/Bienestar y Dashboard directivo (con auto-refresh y trazabilidad).
> **Build verificado** (`pnpm run build`, 42 módulos) y **render verificado** en navegador
> (login + navegación + manejo de errores). Conecta a las APIs vía proxy de Vite (el gateway
> asume ese rol en el Paso 8).

**Objetivo:** que la experiencia principal ocurra **desde interfaces**, no desde Postman.

- **Portal Académico/Secretaría:** registrar estudiante, matrícula, ficha, estado, historial.
- **Portal Financiero:** consultar matriculados, registrar deuda, confirmar pago, ver pendientes.
- **Portal Docente/Bienestar:** consultar estudiantes, registrar asistencia/incidente, historial.
- **Dashboard directivo** (propio o Metabase/Superset/Streamlit/Power BI) con: total
  matriculados, pagos confirmados/pendientes, asistencias, incidentes, eventos procesados,
  errores/fallidos y estado general.

**Entregable:** 3 portales operables + 1 dashboard alimentado con datos reales del ecosistema.

---

## Paso 8 — API Gateway + Seguridad (Semana 4-5)

**Objetivo:** entrada centralizada y control de acceso.

- Poner **API Gateway** (Kong, NGINX, Spring Cloud Gateway, Express Gateway u otro) como
  punto único de entrada a todas las APIs.
- **Seguridad**: autenticación/autorización con **JWT o API Key**; usuarios de prueba por rol
  (secretaría, finanzas, docente, dirección).
- Rutear los frontends a través del gateway.

**Entregable:** todo el tráfico pasa por el gateway y requiere credenciales válidas.

---

## Paso 9 — Resiliencia, idempotencia y observabilidad (Semana 5)

**Objetivo:** cubrir el requisito obligatorio de falla controlada y trazabilidad.

- Implementar al menos un mecanismo de resiliencia: **reintentos + Dead Letter Queue (DLQ)**,
  registro de error y **estado de mensaje fallido**.
- **Idempotencia** en al menos un flujo crítico (Idempotent Receiver — evitar reprocesar pagos).
- **Message Translator** donde se transforme entre modelos/formatos.
- **Observabilidad**: logs con `correlationId`, **health checks** por servicio, trazabilidad
  del flujo completo.
- Preparar y ensayar **un escenario de falla** (p. ej. Notificaciones caído → mensaje a DLQ →
  reprocesamiento) para la defensa.

**Entregable:** demo reproducible de falla + recuperación con trazabilidad end-to-end.

---

## Paso 10 — Documentación, pruebas, ensayo de demo y defensa (Semana 6)

**Objetivo:** cerrar entregables y preparar la evaluación de la semana 16.

- **Documento de arquitectura** (máx. 10 pág.): problema, alcance, actores, diagramas,
  servicios, contratos de API y eventos, patrones aplicados, decisiones, seguridad,
  resiliencia, observabilidad, integración de datos, limitaciones, mejoras futuras y
  **declaración de uso de IA**.
- **Bitácora de trabajo**: integrantes, responsabilidades, decisiones, problemas, herramientas.
- Colección **Postman** (solo respaldo), datos semilla, usuarios de prueba, README con
  instrucciones de instalación/ejecución y variables de ejemplo.
- Ensayar el guion **"Un día de operación en CampusConnect 360"** (matrícula → pago →
  asistencia → dashboard → falla → trazabilidad), máximo 15 min.
- **Repartir la defensa técnica**: cada integrante debe poder explicar y modificar su parte.

**Entregable:** repositorio + documentos + demo ensayada listos para la defensa.

---

## Checklist de requisitos obligatorios (control final)

- [ ] Mínimo 3 frontends funcionales + 1 dashboard
- [ ] APIs REST documentadas con Swagger/OpenAPI
- [ ] API Gateway como entrada centralizada
- [ ] Seguridad (JWT / API Key)
- [ ] Mensajería con RabbitMQ o Kafka
- [ ] Mínimo 4 eventos de negocio
- [ ] Bases de datos / esquemas separados por servicio
- [ ] Integración de datos (ETL / CQRS) que alimenta analítica
- [ ] Resiliencia: reintentos / idempotencia / DLQ
- [ ] Observabilidad: logs / health checks / trazabilidad
- [ ] Docker Compose funcional
- [ ] Documentación completa + bitácora + declaración de IA
- [ ] Demo en vivo desde interfaces (no solo Postman)
- [ ] Al menos un escenario de falla controlada demostrable

## Mapa rápido peso de rúbrica → paso

| Criterio | Peso | Paso(s) |
|---|---|---|
| Análisis del problema y modelado del dominio | 10% | 1 |
| Diseño arquitectónico y justificación | 15% | 1 |
| Ecosistema funcional con frontends operables | 15% | 7 |
| APIs, API Gateway y seguridad | 10% | 3,5,6,8 |
| Mensajería, eventos y patrones de integración | 15% | 3,4,5,6 |
| Resiliencia, idempotencia, errores y DLQ | 10% | 9 |
| Integración de datos, dashboard y trazabilidad | 10% | 4,7,9 |
| Contenerización, despliegue y facilidad de ejecución | 5% | 2 |
| Documentación técnica y decisiones | 5% | 10 |
| Presentación, demo y defensa técnica | 5% | 10 |
