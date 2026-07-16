import { useEffect, useState } from 'react'
import { api } from '../api.js'

export default function TeacherPortal() {
  const [students, setStudents] = useState([])
  const [attForm, setAttForm] = useState({ studentId: '', status: 'PRESENT', recordedBy: 'TEA-010' })
  const [incForm, setIncForm] = useState({ studentId: '', category: 'BEHAVIOR', severity: 'MEDIUM', description: '', reportedBy: 'WEL-005' })
  const [history, setHistory] = useState(null)
  const [msg, setMsg] = useState(null)
  const [error, setError] = useState(null)

  async function loadStudents() {
    try { setStudents(await api.get('/attendance/students')) }
    catch (e) { setError(e.message) }
  }

  useEffect(() => { loadStudents() }, [])

  async function submitAttendance(e) {
    e.preventDefault()
    setMsg(null); setError(null)
    try {
      const res = await api.post('/attendance/records', attForm)
      setMsg(`Asistencia ${res.attendanceId} (${res.status}) registrada. Evento AttendanceRecorded publicado.`)
    } catch (e) { setError(e.message) }
  }

  async function submitIncident(e) {
    e.preventDefault()
    setMsg(null); setError(null)
    try {
      const res = await api.post('/attendance/incidents', incForm)
      setMsg(`Incidente ${res.incidentId} (${res.severity}) registrado. Evento IncidentReported publicado.`)
    } catch (e) { setError(e.message) }
  }

  async function loadHistory(id) {
    if (!id) { setHistory(null); return }
    try { setHistory(await api.get(`/attendance/students/${id}/history`)) }
    catch (e) { setError(e.message) }
  }

  return (
    <div>
      <h2>Portal Docente / Bienestar</h2>
      <p className="muted">Registrar asistencia e incidentes; consultar historial.</p>

      {msg && <div className="alert success">{msg}</div>}
      {error && <div className="alert error">{error}</div>}

      <div className="grid-2">
        <section className="card">
          <h3>Registrar asistencia</h3>
          <form onSubmit={submitAttendance} className="form">
            <label>Estudiante
              <select required value={attForm.studentId}
                onChange={(e) => setAttForm({ ...attForm, studentId: e.target.value })}>
                <option value="">Selecciona…</option>
                {students.map((s) => (
                  <option key={s.studentId} value={s.studentId}>{s.studentId} — {s.firstName} {s.lastName}</option>
                ))}
              </select>
            </label>
            <label>Estado
              <select value={attForm.status} onChange={(e) => setAttForm({ ...attForm, status: e.target.value })}>
                <option>PRESENT</option><option>ABSENT</option><option>LATE</option>
              </select>
            </label>
            <button className="btn primary" type="submit">Registrar asistencia</button>
          </form>
        </section>

        <section className="card">
          <h3>Registrar incidente / novedad</h3>
          <form onSubmit={submitIncident} className="form">
            <label>Estudiante
              <select required value={incForm.studentId}
                onChange={(e) => setIncForm({ ...incForm, studentId: e.target.value })}>
                <option value="">Selecciona…</option>
                {students.map((s) => (
                  <option key={s.studentId} value={s.studentId}>{s.studentId} — {s.firstName} {s.lastName}</option>
                ))}
              </select>
            </label>
            <label>Categoría
              <select value={incForm.category} onChange={(e) => setIncForm({ ...incForm, category: e.target.value })}>
                <option>BEHAVIOR</option><option>HEALTH</option><option>ACADEMIC</option>
              </select>
            </label>
            <label>Severidad
              <select value={incForm.severity} onChange={(e) => setIncForm({ ...incForm, severity: e.target.value })}>
                <option>LOW</option><option>MEDIUM</option><option>HIGH</option>
              </select>
            </label>
            <label>Descripción
              <textarea value={incForm.description}
                onChange={(e) => setIncForm({ ...incForm, description: e.target.value })} />
            </label>
            <button className="btn primary" type="submit">Registrar incidente</button>
          </form>
        </section>
      </div>

      <section className="card">
        <h3>Historial por estudiante</h3>
        <select onChange={(e) => loadHistory(e.target.value)} defaultValue="">
          <option value="">Selecciona un estudiante…</option>
          {students.map((s) => (
            <option key={s.studentId} value={s.studentId}>{s.studentId} — {s.firstName} {s.lastName}</option>
          ))}
        </select>
        {history && (
          <div className="grid-2" style={{ marginTop: '1rem' }}>
            <div>
              <h4>Asistencia</h4>
              <table className="table">
                <thead><tr><th>ID</th><th>Fecha</th><th>Estado</th></tr></thead>
                <tbody>
                  {history.attendance.map((a) => (
                    <tr key={a.attendanceId}><td>{a.attendanceId}</td><td>{a.date}</td><td>{a.status}</td></tr>
                  ))}
                  {history.attendance.length === 0 && <tr><td colSpan="3" className="muted">Sin registros</td></tr>}
                </tbody>
              </table>
            </div>
            <div>
              <h4>Incidentes</h4>
              <table className="table">
                <thead><tr><th>ID</th><th>Categoría</th><th>Severidad</th></tr></thead>
                <tbody>
                  {history.incidents.map((i) => (
                    <tr key={i.incidentId}><td>{i.incidentId}</td><td>{i.category}</td><td>{i.severity}</td></tr>
                  ))}
                  {history.incidents.length === 0 && <tr><td colSpan="3" className="muted">Sin incidentes</td></tr>}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </section>
    </div>
  )
}
