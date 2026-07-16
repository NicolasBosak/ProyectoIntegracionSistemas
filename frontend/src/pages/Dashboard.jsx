import { useEffect, useState, useCallback } from 'react'
import { api } from '../api.js'

const CARDS = [
  { key: 'totalEnrolled', label: 'Estudiantes matriculados' },
  { key: 'paymentsConfirmed', label: 'Pagos confirmados' },
  { key: 'paymentsPending', label: 'Pagos pendientes' },
  { key: 'attendanceRecords', label: 'Asistencias registradas' },
  { key: 'incidentsReported', label: 'Incidentes reportados' },
  { key: 'eventsProcessed', label: 'Eventos procesados' },
  { key: 'failedMessages', label: 'Mensajes fallidos' }
]

export default function Dashboard() {
  const [kpis, setKpis] = useState(null)
  const [events, setEvents] = useState([])
  const [error, setError] = useState(null)

  const load = useCallback(async () => {
    try {
      setKpis(await api.get('/analytics/dashboard'))
      setEvents(await api.get('/analytics/events'))
      setError(null)
    } catch (e) { setError(e.message) }
  }, [])

  useEffect(() => {
    load()
    const id = setInterval(load, 4000) // refresco automatico
    return () => clearInterval(id)
  }, [load])

  return (
    <div>
      <div className="dash-head">
        <h2>Dashboard Directivo</h2>
        <span className="muted">Actualización automática cada 4s</span>
      </div>

      {error && <div className="alert error">{error}</div>}

      {kpis && (
        <>
          <div className={'status-banner ' + (kpis.ecosystemStatus === 'HEALTHY' ? 'ok' : 'warn')}>
            Estado del ecosistema: <b>{kpis.ecosystemStatus}</b>
          </div>
          <div className="kpi-grid">
            {CARDS.map((c) => (
              <div className="kpi" key={c.key}>
                <div className="kpi-value">{kpis[c.key]}</div>
                <div className="kpi-label">{c.label}</div>
              </div>
            ))}
          </div>
        </>
      )}

      <section className="card">
        <h3>Eventos recientes (trazabilidad)</h3>
        <table className="table">
          <thead><tr><th>Tipo</th><th>Origen</th><th>Correlación</th><th>Ocurrido</th></tr></thead>
          <tbody>
            {events.map((ev) => (
              <tr key={ev.eventId}>
                <td>{ev.eventType}</td>
                <td>{ev.source}</td>
                <td className="mono">{ev.correlationId}</td>
                <td>{ev.occurredAt}</td>
              </tr>
            ))}
            {events.length === 0 && <tr><td colSpan="4" className="muted">Sin eventos aún</td></tr>}
          </tbody>
        </table>
      </section>
    </div>
  )
}
