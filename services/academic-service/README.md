# Servicio Académico

Microservicio Spring Boot. Responsabilidad: estudiantes, matrículas y estado académico/financiero.

- **BD:** `academic_db` · **Puerto:** 8091 (interno 8080)
- **Publica:** `StudentEnrolled`, `StudentStatusUpdated`
- **Consume:** `PaymentConfirmed` (cola `q.academic.payment`, con idempotencia)
- **API:** `/academic/*` — ver [contratos de API](../../docs/03-contratos-api.md)

> Se implementa en el **Paso 3** del plan. Aquí irá el proyecto Spring Boot (`pom.xml`, `src/`, `Dockerfile`).
