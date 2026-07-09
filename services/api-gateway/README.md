# API Gateway

Spring Cloud Gateway. Entrada única al ecosistema: valida **JWT**, aplica roles y enruta a los microservicios.

- **Puerto:** 8080 (entrada pública del sistema)
- **Rutas:** `/auth/*`, `/academic/*`, `/payments/*`, `/attendance/*`, `/notifications/*`, `/analytics/*`
- Ver [contratos de API](../../docs/03-contratos-api.md) y [arquitectura](../../docs/01-arquitectura.md)

> Se implementa en el **Paso 8** del plan. Aquí irá el proyecto Spring Boot (`pom.xml`, `src/`, `Dockerfile`).
