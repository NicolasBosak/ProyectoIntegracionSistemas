# Documentación — CampusConnect 360

Índice de los documentos de diseño (crecerá con cada paso del plan).

## Paso 1 — Arquitectura, dominio y contratos ✅

| Documento | Contenido |
|---|---|
| [01-arquitectura.md](01-arquitectura.md) | Problema, alcance, actores, diagramas, servicios, decisiones (ADR), seguridad, resiliencia, observabilidad |
| [02-contratos-eventos.md](02-contratos-eventos.md) | Envelope común + 4 eventos obligatorios y 3 complementarios (JSON) |
| [03-contratos-api.md](03-contratos-api.md) | Rutas REST por servicio, roles y ejemplos de request/response |
| [04-mensajeria-rabbitmq.md](04-mensajeria-rabbitmq.md) | Exchanges, colas, routing keys, DLQ y mapeo de patrones |

## Decisiones de stack fijadas

- **Backend:** Spring Boot (Java)
- **Mensajería:** RabbitMQ (topic exchange + DLX)
- **Frontend:** React (3 portales + dashboard directivo propio)
- **API Gateway:** Spring Cloud Gateway (JWT)
- **Persistencia:** PostgreSQL (una BD por servicio)
- **Contenedores:** Docker Compose

> Diagramas en formato Mermaid: se renderizan directamente en GitHub y en la mayoría de
> visores Markdown.
