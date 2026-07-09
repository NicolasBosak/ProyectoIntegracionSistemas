# Convenciones de trabajo — CampusConnect 360

Estas convenciones respaldan la **evidencia de autoría y proceso** que exige la consigna
(historial de commits, participación visible, construcción progresiva).

## Ramas (Git Flow ligero)

- `main` — estable, siempre levanta con `docker compose up`.
- `develop` — integración del trabajo en curso.
- `feature/<servicio>-<descripcion>` — una rama por funcionalidad. Ej: `feature/academic-student-enrollment`.

Flujo: `feature/*` → PR hacia `develop` → al cerrar un paso del plan, `develop` → `main`.

## Commits (Conventional Commits)

Formato: `tipo(alcance): descripción breve en presente`

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de error |
| `docs` | Documentación |
| `chore` | Configuración, infraestructura, dependencias |
| `refactor` | Cambio interno sin alterar comportamiento |
| `test` | Pruebas |

Ejemplos:
```
feat(academic): publicar evento StudentEnrolled al matricular
chore(infra): docker-compose con RabbitMQ y PostgreSQL
docs(arquitectura): agregar diagrama de flujo de eventos
```

**Alcances (`scope`) sugeridos:** `academic`, `payment`, `notification`, `attendance`,
`analytics`, `gateway`, `frontend`, `infra`, `docs`.

## Buenas prácticas

- Commits pequeños y frecuentes (evidencia de construcción progresiva).
- Cada integrante commitea su propio trabajo (participación visible).
- PRs revisados por al menos otro integrante antes de mergear a `develop`.
- No subir `.env` ni secretos; usar `.env.example`.
- Actualizar [BITACORA.md](BITACORA.md) al cerrar cada paso o decisión importante.

## Declaración de uso de IA

Todo uso de IA generativa (código base, formularios, documentación, diagramas) se registra en
la [bitácora](BITACORA.md). El grupo comprende, adapta y **defiende** todo el código entregado.
