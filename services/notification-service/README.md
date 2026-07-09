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

> El reproceso desde la DLQ (`POST /notifications/{id}/reprocess`) se implementa en el **Paso 9**.

## Nota técnica

El conversor JSON usa **type precedence INFERRED**: deserializa según el tipo del método
`@RabbitListener` (`IncomingEvent`) en vez del header `__TypeId__` que envía el productor.
Así los servicios se mantienen desacoplados sin compartir clases.
