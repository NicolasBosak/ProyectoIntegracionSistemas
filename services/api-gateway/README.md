# API Gateway

Spring Cloud Gateway. **Entrada única** al ecosistema (`:8080`): valida **JWT**, aplica
autorización por rol y enruta a los microservicios.

## Rutas

| Prefijo | Servicio destino | Rol requerido |
|---|---|---|
| `/auth/**` | (gateway) login / me | público |
| `/academic/**` | academic-service | SECRETARIA |
| `/payments/**` | payment-service | FINANZAS |
| `/attendance/**` | attendance-service | DOCENTE |
| `/analytics/**` | analytics-service | DIRECCION |
| `/notifications/**` | notification-service | DIRECCION |

## Seguridad

- `POST /auth/login` `{username, password}` → `{token, role, expiresIn}` (JWT HS256).
- Toda petición a los servicios exige `Authorization: Bearer <token>`; si falta/expira → 401.
- Si el rol del token no corresponde al prefijo → 403.
- El gateway propaga `X-User` y `X-Role` a los servicios downstream.

### Usuarios de prueba

| Usuario | Clave | Rol |
|---|---|---|
| `secretaria` | `demo123` | SECRETARIA |
| `finanzas` | `demo123` | FINANZAS |
| `docente` | `demo123` | DOCENTE |
| `direccion` | `demo123` | DIRECCION |

## Probar

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"secretaria","password":"demo123"}' | jq -r .token)

# 2. Llamada autenticada a través del gateway
curl http://localhost:8080/academic/students -H "Authorization: Bearer $TOKEN"

# 3. Sin token -> 401
curl -i http://localhost:8080/academic/students
```

> El secreto JWT se configura con la variable de entorno `JWT_SECRET` (mínimo 32 caracteres).
