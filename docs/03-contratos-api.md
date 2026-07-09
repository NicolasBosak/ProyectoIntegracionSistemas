# Contratos de API — CampusConnect 360

> Paso 1 · Definición REST previa a la implementación. Todas las rutas pasan por el
> **API Gateway** (`http://localhost:8080`) y requieren **JWT** (salvo login).
> Cada servicio publicará su documentación **Swagger/OpenAPI**.

## Convenciones

- Base a través del gateway: `/{servicio}/...` → el gateway enruta al microservicio.
- Autenticación: header `Authorization: Bearer <jwt>`.
- Errores estándar: `{ "timestamp", "status", "error", "message", "correlationId" }`.
- Todas las respuestas incluyen header `X-Correlation-Id`.

---

## Autenticación (Gateway / Auth)

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/auth/login` | público | Devuelve JWT `{ token, role, expiresIn }` |
| GET | `/auth/me` | cualquiera | Datos del usuario autenticado |

```json
// POST /auth/login
{ "username": "secretaria01", "password": "demo123" }
// 200 OK
{ "token": "eyJ...", "role": "SECRETARIA", "expiresIn": 3600 }
```

---

## Servicio Académico  `/academic`  (rol SECRETARIA)

| Método | Ruta | Descripción | Efecto |
|---|---|---|---|
| POST | `/academic/students` | Registrar estudiante | guarda + publica `StudentEnrolled` |
| POST | `/academic/students/{id}/enrollment` | Crear/confirmar matrícula | actualiza matrícula |
| GET | `/academic/students/{id}` | Ficha del estudiante | — |
| GET | `/academic/students` | Listar estudiantes matriculados | — |
| GET | `/academic/students/{id}/status` | Estado académico y financiero | — |
| GET | `/academic/students/{id}/events` | Historial de eventos asociados | — |

```json
// POST /academic/students
{
  "firstName": "Ana", "lastName": "Pérez",
  "schoolId": "SCH-001", "grade": "8vo EGB",
  "guardianEmail": "representante@example.com"
}
// 201 Created
{ "studentId": "STU-001", "enrollmentId": "ENR-001", "financialStatus": "PENDING" }
```

---

## Servicio de Pagos  `/payments`  (rol FINANZAS)

| Método | Ruta | Descripción | Efecto |
|---|---|---|---|
| GET | `/payments/students` | Estudiantes matriculados (desde Académico) | — |
| POST | `/payments/debts` | Registrar obligación / simular deuda | crea deuda |
| GET | `/payments/pending` | Pagos pendientes | — |
| GET | `/payments/confirmed` | Pagos confirmados | — |
| POST | `/payments/confirm` | Confirmar pago | registra + publica `PaymentConfirmed` |

```json
// POST /payments/confirm
{ "studentId": "STU-001", "amount": 150.00, "concept": "Matrícula", "method": "TRANSFER" }
// 201 Created
{ "paymentId": "PAY-001", "status": "CONFIRMED" }
```

---

## Servicio Asistencia/Bienestar  `/attendance`  (rol DOCENTE)

| Método | Ruta | Descripción | Efecto |
|---|---|---|---|
| GET | `/attendance/students` | Consultar estudiantes | — |
| POST | `/attendance/records` | Registrar asistencia/ausencia | publica `AttendanceRecorded` |
| POST | `/attendance/incidents` | Registrar incidente/novedad | publica `IncidentReported` |
| GET | `/attendance/students/{id}/history` | Historial de registros | — |

```json
// POST /attendance/records
{ "studentId": "STU-001", "date": "2026-07-15", "status": "PRESENT" }
// POST /attendance/incidents
{ "studentId": "STU-001", "category": "BEHAVIOR", "severity": "MEDIUM", "description": "..." }
```

---

## Servicio de Notificaciones  `/notifications`  (interno / DIRECCION)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/notifications` | Listar notificaciones simuladas |
| GET | `/notifications/failed` | Notificaciones fallidas (DLQ / estado) |
| POST | `/notifications/{id}/reprocess` | Reprocesar una notificación fallida |

---

## Servicio de Analítica  `/analytics`  (rol DIRECCION)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/analytics/dashboard` | KPIs consolidados del ecosistema |
| GET | `/analytics/events` | Eventos procesados (trazabilidad) |

```json
// GET /analytics/dashboard
{
  "totalEnrolled": 42,
  "paymentsConfirmed": 30,
  "paymentsPending": 12,
  "attendanceRecords": 128,
  "incidentsReported": 4,
  "eventsProcessed": 210,
  "failedMessages": 2,
  "ecosystemStatus": "HEALTHY"
}
```

---

## Health checks (todos los servicios)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/{servicio}/actuator/health` | Estado del servicio (Spring Boot Actuator) |

---

## Matriz rol → portal → API

| Rol | Portal | APIs que consume |
|---|---|---|
| SECRETARIA | Académico | `/academic/*` |
| FINANZAS | Financiero | `/payments/*`, `/academic/students` (lectura) |
| DOCENTE | Docente/Bienestar | `/attendance/*` |
| DIRECCION | Dashboard | `/analytics/*`, `/notifications` |
