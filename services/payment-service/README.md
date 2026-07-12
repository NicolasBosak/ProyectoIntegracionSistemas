# Servicio de Pagos

Microservicio Spring Boot. Gestiona deudas y confirmación de pagos; **publica** `PaymentConfirmed`.

- **BD:** `payment_db` · **Puerto host:** 8092 (interno 8080)
- **Consume:** `StudentEnrolled` → cola `q.payments.student` (crea deuda de matrícula + proyección local)
- **Publica:** `PaymentConfirmed` (routing key `payment.confirmed`)
- **Swagger UI:** http://localhost:8092/swagger-ui.html · **Health:** `/actuator/health`

## Endpoints (`/payments`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/payments/students` | Estudiantes matriculados (proyección local desde eventos) |
| POST | `/payments/debts` | Registrar obligación / simular deuda |
| GET | `/payments/pending` | Pagos pendientes |
| GET | `/payments/confirmed` | Pagos confirmados |
| POST | `/payments/confirm` | Confirmar pago → publica `PaymentConfirmed` |

## Diseño event-driven

En vez de llamar por HTTP al servicio Académico, Pagos **consume `StudentEnrolled`** y
construye su propia proyección de estudiantes (`student_refs`) creando automáticamente la
deuda de matrícula (`Matricula`, monto por defecto configurable). Esto:
- desacopla los servicios (no hay dependencia síncrona),
- suma otra evidencia de **Publish/Subscribe** (StudentEnrolled → Académico proyección,
  Notificaciones, Analítica **y** Pagos),
- **conserva el `correlationId`** de la matrícula en el pago, encadenando la trazabilidad
  matrícula → pago → actualización de estado.

## Confirmar un pago (evidencia)

```bash
curl -X POST http://localhost:8092/payments/confirm \
  -H "Content-Type: application/json" \
  -d '{"studentId":"STU-004","amount":150.00,"concept":"Matricula","method":"TRANSFER"}'
```
Al confirmar se publica `PaymentConfirmed`, consumido por **Académico** (actualiza estado
financiero), **Notificaciones** (aviso) y **Analítica** (indicadores).
