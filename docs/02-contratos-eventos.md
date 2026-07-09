# Contratos de Eventos — CampusConnect 360

> Paso 1 · Estructura común y esquema de cada evento de negocio
> Requisito: mínimo 4 eventos de negocio. Aquí se definen 4 obligatorios + 3 complementarios.

## Estructura común (envelope)

Todo evento comparte una envoltura estándar. Los campos específicos van en `data`.

| Campo | Tipo | Descripción |
|---|---|---|
| `eventId` | string (UUID/`evt-*`) | Identificador único del evento (clave de idempotencia) |
| `eventType` | string | Tipo de evento (ver catálogo) |
| `occurredAt` | string ISO-8601 UTC | Fecha y hora de ocurrencia |
| `correlationId` | string | Id de correlación/trazabilidad del flujo |
| `source` | string | Servicio que publica (`academic-service`, …) |
| `version` | string | Versión del contrato (`1.0`) |
| `data` | object | Datos específicos del evento |

```json
{
  "eventId": "evt-001",
  "eventType": "StudentEnrolled",
  "occurredAt": "2026-07-15T10:30:00Z",
  "correlationId": "corr-20260715-001",
  "source": "academic-service",
  "version": "1.0",
  "data": { "...": "..." }
}
```

## Catálogo de eventos

| Evento | Origen | Routing key | Consumidores | Obligatorio |
|---|---|---|---|---|
| `StudentEnrolled` | Académico | `student.enrolled` | Notificaciones, Analítica | ✅ |
| `PaymentConfirmed` | Pagos | `payment.confirmed` | Académico, Notificaciones, Analítica | ✅ |
| `AttendanceRecorded` | Asistencia | `attendance.recorded` | Analítica (Notificaciones si aplica) | ✅ |
| `IncidentReported` | Asistencia/Bienestar | `incident.reported` | Notificaciones, Analítica | ✅ |
| `NotificationSent` | Notificaciones | `notification.sent` | Analítica | ➕ |
| `NotificationFailed` | Notificaciones | `notification.failed` | Analítica | ➕ |
| `StudentStatusUpdated` | Académico | `student.status.updated` | Notificaciones, Analítica | ➕ |

---

## 1. StudentEnrolled

```json
{
  "eventId": "evt-001",
  "eventType": "StudentEnrolled",
  "occurredAt": "2026-07-15T10:30:00Z",
  "correlationId": "corr-20260715-001",
  "source": "academic-service",
  "version": "1.0",
  "data": {
    "studentId": "STU-001",
    "schoolId": "SCH-001",
    "firstName": "Ana",
    "lastName": "Pérez",
    "grade": "8vo EGB",
    "enrollmentId": "ENR-001",
    "guardianEmail": "representante@example.com"
  }
}
```

## 2. PaymentConfirmed

```json
{
  "eventId": "evt-002",
  "eventType": "PaymentConfirmed",
  "occurredAt": "2026-07-15T11:05:00Z",
  "correlationId": "corr-20260715-001",
  "source": "payment-service",
  "version": "1.0",
  "data": {
    "paymentId": "PAY-001",
    "studentId": "STU-001",
    "amount": 150.00,
    "currency": "USD",
    "concept": "Matrícula",
    "method": "TRANSFER",
    "confirmedAt": "2026-07-15T11:05:00Z"
  }
}
```

## 3. AttendanceRecorded

```json
{
  "eventId": "evt-003",
  "eventType": "AttendanceRecorded",
  "occurredAt": "2026-07-15T12:00:00Z",
  "correlationId": "corr-20260715-002",
  "source": "attendance-service",
  "version": "1.0",
  "data": {
    "attendanceId": "ATT-001",
    "studentId": "STU-001",
    "schoolId": "SCH-001",
    "date": "2026-07-15",
    "status": "PRESENT",
    "recordedBy": "TEA-010"
  }
}
```

> `status` admite: `PRESENT`, `ABSENT`, `LATE`.

## 4. IncidentReported

```json
{
  "eventId": "evt-004",
  "eventType": "IncidentReported",
  "occurredAt": "2026-07-15T12:30:00Z",
  "correlationId": "corr-20260715-003",
  "source": "attendance-service",
  "version": "1.0",
  "data": {
    "incidentId": "INC-001",
    "studentId": "STU-001",
    "schoolId": "SCH-001",
    "category": "BEHAVIOR",
    "severity": "MEDIUM",
    "description": "Novedad registrada por bienestar",
    "reportedBy": "WEL-005"
  }
}
```

> `severity`: `LOW`, `MEDIUM`, `HIGH`. `severity=HIGH` dispara notificación de alerta.

## 5. NotificationSent / 6. NotificationFailed

```json
{
  "eventId": "evt-005",
  "eventType": "NotificationSent",
  "occurredAt": "2026-07-15T10:30:05Z",
  "correlationId": "corr-20260715-001",
  "source": "notification-service",
  "version": "1.0",
  "data": {
    "notificationId": "NOT-001",
    "studentId": "STU-001",
    "channel": "SIMULATED_EMAIL",
    "triggerEvent": "StudentEnrolled",
    "status": "SENT"
  }
}
```

`NotificationFailed` usa la misma estructura con `status: "FAILED"` y un campo
`data.reason` con la causa (para trazar el flujo a la DLQ).

## 7. StudentStatusUpdated

```json
{
  "eventId": "evt-006",
  "eventType": "StudentStatusUpdated",
  "occurredAt": "2026-07-15T11:05:10Z",
  "correlationId": "corr-20260715-001",
  "source": "academic-service",
  "version": "1.0",
  "data": {
    "studentId": "STU-001",
    "financialStatus": "UP_TO_DATE",
    "previousStatus": "PENDING",
    "reason": "PaymentConfirmed"
  }
}
```

---

## Reglas de consumo

- **Idempotencia**: el consumidor guarda `eventId` en `processed_events`; si llega repetido,
  lo descarta (Idempotent Receiver). Aplica al menos en Pagos→Académico.
- **Correlación**: `correlationId` se propaga sin cambios a lo largo de todo el flujo y se
  incluye en cada log (trazabilidad).
- **Message Translator**: al alimentar Analítica, el evento de dominio se transforma al modelo
  de lectura (read model) del dashboard.
