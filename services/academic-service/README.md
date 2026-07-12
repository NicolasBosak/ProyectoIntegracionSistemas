# Servicio Académico

Microservicio Spring Boot. Responsabilidad: estudiantes, matrículas y estado académico/financiero.

- **BD:** `academic_db` · **Puerto host:** 8091 (interno 8080)
- **Publica:** `StudentEnrolled` (routing key `student.enrolled`) · `StudentStatusUpdated` (Paso 5)
- **Consume:** `PaymentConfirmed` → cola `q.academic.payment`, con idempotencia (`processed_events`) → actualiza estado financiero y publica `StudentStatusUpdated`
- **Swagger UI:** http://localhost:8091/swagger-ui.html
- **Health:** http://localhost:8091/actuator/health

## Endpoints (`/academic`)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/academic/students` | Registrar estudiante + matrícula → publica `StudentEnrolled` |
| POST | `/academic/students/{id}/enrollment` | Actualizar/confirmar matrícula |
| GET | `/academic/students` | Listar estudiantes |
| GET | `/academic/students/{id}` | Ficha del estudiante |
| GET | `/academic/students/{id}/status` | Estado académico y financiero |
| GET | `/academic/students/{id}/events` | Historial de eventos del estudiante |

`{id}` es el código de negocio, p. ej. `STU-004`.

## Estructura

```
src/main/java/com/campusconnect/academic/
├── AcademicServiceApplication.java
├── config/        # RabbitMQ (topic exchange) + OpenAPI
├── domain/        # Student, Enrollment, EventLog, FinancialStatus
├── repository/    # Spring Data JPA
├── dto/           # requests/responses (records)
├── event/         # EventEnvelope, StudentEnrolledData, EventPublisher
├── service/       # AcademicService (lógica + publicación de eventos)
├── controller/    # AcademicController (REST)
├── exception/     # manejo de errores (404 / validación)
└── bootstrap/     # DataSeeder (datos semilla)
```

## Ejecutar

```bash
# Con el ecosistema (desde la raíz del repo)
docker compose up -d --build academic-service

# En local (requiere postgres y rabbitmq levantados por compose, JDK 21 + Maven)
cd services/academic-service
mvn spring-boot:run
```

## Probar el flujo (evidencia del evento)

```bash
# 1. Registrar un estudiante (publica StudentEnrolled)
curl -X POST http://localhost:8091/academic/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ana","lastName":"Pérez","schoolId":"SCH-001","grade":"8vo EGB","guardianEmail":"rep@example.com"}'

# 2. Ver el historial de eventos del estudiante creado (STU-00X)
curl http://localhost:8091/academic/students/STU-004/events
```

En el panel de RabbitMQ (http://localhost:15672) se observa el mensaje publicado en el
exchange `campus.events` con routing key `student.enrolled`. Los consumidores (Notificaciones,
Analítica) se conectan en los Pasos 4 y siguientes.
