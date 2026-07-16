# Frontend — CampusConnect 360

SPA en **React + Vite**. Contiene los 3 portales operables y el dashboard directivo, todos
integrados con las APIs de los microservicios.

- **Portal Académico** (SECRETARIA) — registrar estudiante, ficha, historial de eventos
- **Portal Financiero** (FINANZAS) — confirmar pagos, pendientes y confirmados
- **Portal Docente / Bienestar** (DOCENTE) — asistencia, incidentes, historial
- **Dashboard Directivo** (DIRECCION) — KPIs consolidados con auto-refresh + trazabilidad
- **Puerto:** 3000

## Ejecutar (desarrollo)

Requiere Node 20+ y el backend levantado (`docker compose up -d`).

```bash
cd frontend
npm install
npm run dev
```
Abre http://localhost:3000

### Cómo conecta con el backend

En desarrollo, **Vite hace de proxy** (ver `vite.config.js`): el navegador solo habla con
`http://localhost:3000` y cada ruta se reenvía a su microservicio
(`/academic`→8091, `/payments`→8092, `/attendance`→8094, `/analytics`→8095).

> En el **Paso 8** este rol lo asume el **API Gateway** (origen único con JWT) y el frontend
> se conteneriza detrás de él.

## Estructura

```
src/
├── main.jsx, App.jsx        # arranque + rutas
├── api.js                   # cliente fetch (el JWT se añade en el Paso 8)
├── session.js               # rol activo (login real con JWT en el Paso 8)
├── components/Layout.jsx    # barra de navegación
└── pages/                   # Login, AcademicPortal, FinancialPortal, TeacherPortal, Dashboard
```

## Guion de demo ("un día de operación")

1. **Académico** → registrar estudiante (publica `StudentEnrolled`).
2. **Financiero** → confirmar su pago (publica `PaymentConfirmed`).
3. **Docente** → registrar asistencia/incidente (publica `AttendanceRecorded`/`IncidentReported`).
4. **Dashboard** → ver los indicadores actualizarse en vivo y la trazabilidad por `correlationId`.
