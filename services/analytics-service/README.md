# Servicio de Analítica

Microservicio Spring Boot. Mantiene el **read model (CQRS)** que consolida los eventos del
ecosistema y alimenta el **dashboard directivo**.

- **BD:** `analytics_db` · **Puerto host:** 8095 (interno 8080)
- **Consume:** TODOS los eventos → cola `q.analytics.all` (binding `#`)
- **Swagger UI:** http://localhost:8095/swagger-ui.html · **Health:** `/actuator/health`

## Endpoints (`/analytics`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/analytics/dashboard` | KPIs consolidados |
| GET | `/analytics/events` | Últimos 100 eventos procesados (trazabilidad) |

## Read model

- **`metric_counters`** (clave-valor): `ENROLLED`, `PAYMENTS_CONFIRMED`, `ATTENDANCE_RECORDS`,
  `INCIDENTS_REPORTED`, `EVENTS_PROCESSED`, `FAILED_MESSAGES`.
- **`event_records`**: cada evento recibido (trazabilidad). `paymentsPending` se deriva como
  `ENROLLED - PAYMENTS_CONFIRMED`.
- Incluye una **guardia de idempotencia** por `eventId` para no recontar en redeliveries
  (se refuerza en el Paso 9).

## Publish/Subscribe (evidencia)

Un mismo `StudentEnrolled` publicado por Académico llega **a la vez** a:
- `q.notifications.student` → Notificaciones (genera aviso)
- `q.analytics.all` → Analítica (incrementa `ENROLLED`)

Se observa en el panel de RabbitMQ (http://localhost:15672): un mensaje al exchange
`campus.events` se entrega a las dos colas.
