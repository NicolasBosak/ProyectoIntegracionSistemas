# Servicio de Analítica

Microservicio Spring Boot. Responsabilidad: read model (CQRS) que consolida los eventos para el dashboard.

- **BD:** `analytics_db` · **Puerto:** 8095 (interno 8080)
- **Consume:** todos los eventos (cola `q.analytics.all`, binding `#`)
- **API:** `/analytics/*` — ver [contratos de API](../../docs/03-contratos-api.md)

> Se implementa en el **Paso 4** (base) y se enriquece en pasos posteriores. Aquí irá el proyecto Spring Boot.
