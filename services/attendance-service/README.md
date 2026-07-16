# Servicio de Asistencia / Bienestar

Microservicio Spring Boot. Registra asistencia e incidentes; **publica** `AttendanceRecorded`
e `IncidentReported` (completa los 4 eventos de negocio obligatorios).

- **BD:** `attendance_db` · **Puerto host:** 8094 (interno 8080)
- **Consume:** `StudentEnrolled` → cola `q.attendance.student` (proyección local)
- **Publica:** `AttendanceRecorded` (`attendance.recorded`), `IncidentReported` (`incident.reported`)
- **Swagger UI:** http://localhost:8094/swagger-ui.html · **Health:** `/actuator/health`

## Endpoints (`/attendance`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/attendance/students` | Consultar estudiantes |
| POST | `/attendance/records` | Registrar asistencia → `AttendanceRecorded` |
| POST | `/attendance/incidents` | Registrar incidente → `IncidentReported` |
| GET | `/attendance/students/{id}/history` | Historial de asistencia e incidentes |

- `status` (asistencia): `PRESENT`, `ABSENT`, `LATE`.
- `severity` (incidente): `LOW`, `MEDIUM`, `HIGH`.

Al publicar `IncidentReported`, el servicio de Notificaciones (cola `q.notifications.incident`)
genera una alerta simulada; Analítica incrementa `INCIDENTS_REPORTED`.
