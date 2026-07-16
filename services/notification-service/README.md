# Servicio de Notificaciones

Microservicio Spring Boot. **Consume** eventos de negocio y genera notificaciones simuladas;
**publica** `NotificationSent` / `NotificationFailed`.

- **BD:** `notification_db` · **Puerto host:** 8093 (interno 8080)
- **Consume:** `StudentEnrolled`, `PaymentConfirmed`, `IncidentReported`
- **Publica:** `NotificationSent` (`notification.sent`), `NotificationFailed` (`notification.failed`)
- **Swagger UI:** http://localhost:8093/swagger-ui.html · **Health:** `/actuator/health`

## Colas (RabbitMQ)

| Cola | Routing key | DLQ |
|---|---|---|
| `q.notifications.student` | `student.enrolled` | `q.notifications.student.dlq` |
| `q.notifications.payment` | `payment.confirmed` | `q.notifications.payment.dlq` |
| `q.notifications.incident` | `incident.reported` | `q.notifications.incident.dlq` |

Cada cola declara `x-dead-letter-exchange = campus.dlx` (Dead Letter Channel). Con
`default-requeue-rejected: false` y reintentos, un mensaje que falla repetidamente termina en su DLQ.

## Endpoints (`/notifications`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/notifications` | Listar notificaciones |
| GET | `/notifications/failed` | Notificaciones fallidas |
| GET | `/notifications/dead-letters` | Mensajes en la DLQ (estado consultable) |
| POST | `/notifications/dead-letters/{id}/reprocess` | Reprocesar un mensaje fallido |

## Resiliencia (Paso 9)

- **Reintentos**: `max-attempts: 3` en el listener antes de rechazar el mensaje.
- **Dead Letter Channel**: los mensajes rechazados van a `q.notifications.*.dlq`; un
  `DeadLetterListener` los registra en la tabla `dead_letters` y publica `NotificationFailed`.
- **Reproceso**: `POST /notifications/dead-letters/{id}/reprocess` reenvía el mensaje al flujo
  normal (idempotente: si ya fue reprocesado, no repite).
- **Modo caos** (`CAMPUS_CHAOS=true`): las notificaciones de incidentes fallan a propósito para
  demostrar el escenario de falla → reintentos → DLQ. Por defecto está desactivado.

### Demo del escenario de falla

```bash
# Levantar el servicio con caos activo
CAMPUS_CHAOS=true docker compose up -d notification-service
# Registrar un incidente (Portal Docente) -> la notificación falla 3 veces -> DLQ
curl http://localhost:8093/notifications/dead-letters      # aparece el mensaje fallido
# Reprocesar
curl -X POST http://localhost:8093/notifications/dead-letters/1/reprocess
```

## Nota técnica

El conversor JSON usa **type precedence INFERRED**: deserializa según el tipo del método
`@RabbitListener` (`IncomingEvent`) en vez del header `__TypeId__` que envía el productor.
Así los servicios se mantienen desacoplados sin compartir clases.
