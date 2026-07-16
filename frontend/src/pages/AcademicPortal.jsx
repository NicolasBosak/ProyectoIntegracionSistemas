import { useEffect, useState } from 'react'
import { api } from '../api.js'

const EMPTY = { firstName: '', lastName: '', schoolId: 'SCH-001', grade: '8vo EGB', guardianEmail: '' }

export default function AcademicPortal() {
  const [form, setForm] = useState(EMPTY)
  const [students, setStudents] = useState([])
  const [selected, setSelected] = useState(null)
  const [events, setEvents] = useState([])
  const [msg, setMsg] = useState(null)
  const [error, setError] = useState(null)

  async function loadStudents() {
    try {
      setStudents(await api.get('/academic/students'))
    } catch (e) { setError(e.message) }
  }

  useEffect(() => { loadStudents() }, [])

  async function register(e) {
    e.preventDefault()
    setMsg(null); setError(null)
    try {
      const res = await api.post('/academic/students', form)
      setMsg(`Estudiante ${res.studentId} matriculado (matrícula ${res.enrollmentId}). Evento StudentEnrolled publicado.`)
      setForm(EMPTY)
      loadStudents()
    } catch (e) { setError(e.message) }
  }

  async function openStudent(id) {
    setError(null)
    try {
      const ficha = await api.get(`/academic/students/${id}`)
      const evts = await api.get(`/academic/students/${id}/events`)
      setSelected(ficha)
      setEvents(evts)
    } catch (e) { setError(e.message) }
  }

  return (
    <div>
      <h2>Portal Académico / Secretaría</h2>
      <p className="muted">Registrar estudiantes, consultar fichas e historial de eventos.</p>

      {msg && <div className="alert success">{msg}</div>}
      {error && <div className="alert error">{error}</div>}

      <div className="grid-2">
        <section className="card">
          <h3>Registrar estudiante</h3>
          <form onSubmit={register} className="form">
            <label>Nombres
              <input required value={form.firstName}
                onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
            </label>
            <label>Apellidos
              <input required value={form.lastName}
                onChange={(e) => setForm({ ...form, lastName: e.target.value })} />
            </label>
            <label>Colegio (schoolId)
              <input required value={form.schoolId}
                onChange={(e) => setForm({ ...form, schoolId: e.target.value })} />
            </label>
            <label>Grado
              <input required value={form.grade}
                onChange={(e) => setForm({ ...form, grade: e.target.value })} />
            </label>
            <label>Email del representante
              <input type="email" value={form.guardianEmail}
                onChange={(e) => setForm({ ...form, guardianEmail: e.target.value })} />
            </label>
            <button className="btn primary" type="submit">Registrar y matricular</button>
          </form>
        </section>

        <section className="card">
          <h3>Estudiantes matriculados</h3>
          <table className="table">
            <thead>
              <tr><th>ID</th><th>Nombre</th><th>Grado</th><th>Financiero</th></tr>
            </thead>
            <tbody>
              {students.map((s) => (
                <tr key={s.studentId} className="clickable" onClick={() => openStudent(s.studentId)}>
                  <td>{s.studentId}</td>
                  <td>{s.firstName} {s.lastName}</td>
                  <td>{s.grade}</td>
                  <td><span className={'pill ' + s.financialStatus}>{s.financialStatus}</span></td>
                </tr>
              ))}
              {students.length === 0 && <tr><td colSpan="4" className="muted">Sin estudiantes aún</td></tr>}
            </tbody>
          </table>
        </section>
      </div>

      {selected && (
        <section className="card">
          <h3>Ficha de {selected.studentId}</h3>
          <div className="ficha">
            <div><b>Nombre:</b> {selected.firstName} {selected.lastName}</div>
            <div><b>Colegio:</b> {selected.schoolId}</div>
            <div><b>Grado:</b> {selected.grade}</div>
            <div><b>Estado académico:</b> {selected.academicStatus}</div>
            <div><b>Estado financiero:</b> <span className={'pill ' + selected.financialStatus}>{selected.financialStatus}</span></div>
          </div>
          <h4>Historial de eventos</h4>
          <table className="table">
            <thead><tr><th>Tipo</th><th>Correlación</th><th>Fecha</th></tr></thead>
            <tbody>
              {events.map((ev) => (
                <tr key={ev.eventId}>
                  <td>{ev.eventType}</td>
                  <td className="mono">{ev.correlationId}</td>
                  <td>{ev.occurredAt}</td>
                </tr>
              ))}
              {events.length === 0 && <tr><td colSpan="3" className="muted">Sin eventos</td></tr>}
            </tbody>
          </table>
        </section>
      )}
    </div>
  )
}
