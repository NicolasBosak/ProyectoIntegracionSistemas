# CampusConnect 360

> Ecosistema funcional de integración para una red de colegios.
> Proyecto Integrador Final — Integración de Sistemas.

Integra los sistemas heterogéneos de una red de colegios (académico, pagos, asistencia,
notificaciones y analítica) mediante **APIs REST**, un **API Gateway**, **mensajería basada en
eventos** (RabbitMQ), integración de datos, seguridad, resiliencia y observabilidad.

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | React (3 portales + dashboard) |
| Backend | Spring Boot (Java) — 5 microservicios |
| API Gateway | Spring Cloud Gateway (JWT) |
| Mensajería | RabbitMQ (topic exchange + DLQ) |
| Persistencia | PostgreSQL (una BD por servicio) |
| Contenedores | Docker Compose |

## Arquitectura

Ver documentación completa en [`docs/`](docs/README.md):
arquitectura, contratos de eventos y de API, y topología de mensajería.

```
Frontend React ──► API Gateway (JWT) ──► [Académico | Pagos | Asistencia | Analítica]
                                              │            │           │
                                              └──── RabbitMQ (eventos) ─┘──► Notificaciones
```

## Estructura del repositorio

```
.
├── docker-compose.yml          # Orquestación (infra activa; servicios por habilitar)
├── .env.example                # Variables de entorno de ejemplo
├── infra/postgres/init/        # Script que crea una BD por servicio
├── services/
│   ├── api-gateway/            # Spring Cloud Gateway (Paso 8)
│   ├── academic-service/       # Servicio Académico (Paso 3)
│   ├── payment-service/        # Servicio de Pagos (Paso 5)
│   ├── notification-service/   # Servicio de Notificaciones (Paso 4)
│   ├── attendance-service/     # Servicio Asistencia/Bienestar (Paso 6)
│   └── analytics-service/      # Servicio de Analítica / CQRS (Paso 4)
├── frontend/                   # SPA React: portales + dashboard (Paso 7)
├── docs/                       # Documentación de arquitectura y contratos
├── PLAN.md                     # Plan de 10 pasos
├── CONVENCIONES.md             # Convenciones de commits y ramas
└── BITACORA.md                 # Bitácora de trabajo y responsabilidades
```

## Requisitos previos

- Docker y Docker Compose
- (Para desarrollo) JDK 21 y Node.js 20+

## Puesta en marcha (estado actual — Paso 2)

```bash
# 1. Copia las variables de entorno
cp .env.example .env

# 2. Levanta la infraestructura base (RabbitMQ + PostgreSQL + Adminer)
docker compose up -d

# 3. Verifica el estado
docker compose ps
```

### Accesos

| Servicio | URL | Credenciales |
|---|---|---|
| Panel RabbitMQ | http://localhost:15672 | `campus` / `campus123` (según `.env`) |
| Adminer (BD) | http://localhost:8081 | Sistema: PostgreSQL · Servidor: `postgres` · Usuario: `campus` |

Al arrancar, PostgreSQL crea automáticamente las bases: `academic_db`, `payment_db`,
`notification_db`, `attendance_db`, `analytics_db`.

```bash
# Detener
docker compose down          # conserva los datos
docker compose down -v       # elimina también los volúmenes (reinicio limpio)
```

## Estado del proyecto

Ver avance en [PLAN.md](PLAN.md). Actualmente: **Paso 2 — infraestructura base operativa**.
