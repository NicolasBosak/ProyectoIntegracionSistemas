# Servicio de Notificaciones

Microservicio Spring Boot. Responsabilidad: notificaciones simuladas a representantes.

- **BD:** `notification_db` · **Puerto:** 8093 (interno 8080)
- **Publica:** `NotificationSent`, `NotificationFailed`
- **Consume:** `StudentEnrolled`, `PaymentConfirmed`, `IncidentReported` (colas `q.notifications.*`)
- **API:** `/notifications/*` — ver [contratos de API](../../docs/03-contratos-api.md)

> Se implementa en el **Paso 4** del plan. Aquí irá el proyecto Spring Boot (`pom.xml`, `src/`, `Dockerfile`).
