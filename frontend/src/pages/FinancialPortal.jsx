import { useEffect, useState } from 'react'
import { api } from '../api.js'

export default function FinancialPortal() {
  const [students, setStudents] = useState([])
  const [pending, setPending] = useState([])
  const [confirmed, setConfirmed] = useState([])
  const [form, setForm] = useState({ studentId: '', amount: '150.00', concept: 'Matricula', method: 'TRANSFER' })
  const [msg, setMsg] = useState(null)
  const [error, setError] = useState(null)

  async function loadAll() {
    try {
      setStudents(await api.get('/payments/students'))
      setPending(await api.get('/payments/pending'))
      setConfirmed(await api.get('/payments/confirmed'))
    } catch (e) { setError(e.message) }
  }

  useEffect(() => { loadAll() }, [])

  async function confirm(e) {
    e.preventDefault()
    setMsg(null); setError(null)
    try {
      const res = await api.post('/payments/confirm', {
        studentId: form.studentId,
        amount: parseFloat(form.amount),
        concept: form.concept,
        method: form.method
      })
      setMsg(`Pago ${res.paymentId} confirmado para ${res.studentId}. Evento PaymentConfirmed publicado.`)
      loadAll()
    } catch (e) { setError(e.message) }
  }

  return (
    <div>
      <h2>Portal Financiero / Pagos</h2>
      <p className="muted">Confirmar pagos y consultar pendientes y confirmados.</p>

      {msg && <div className="alert success">{msg}</div>}
      {error && <div className="alert error">{error}</div>}

      <div className="grid-2">
        <section className="card">
          <h3>Confirmar pago</h3>
          <form onSubmit={confirm} className="form">
            <label>Estudiante
              <select required value={form.studentId}
                onChange={(e) => setForm({ ...form, studentId: e.target.value })}>
                <option value="">Selecciona…</option>
                {students.map((s) => (
                  <option key={s.studentId} value={s.studentId}>
                    {s.studentId} — {s.firstName} {s.lastName}
                  </option>
                ))}
              </select>
            </label>
            <label>Monto
              <input type="number" step="0.01" required value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })} />
            </label>
            <label>Concepto
              <input value={form.concept}
                onChange={(e) => setForm({ ...form, concept: e.target.value })} />
            </label>
            <label>Método
              <select value={form.method} onChange={(e) => setForm({ ...form, method: e.target.value })}>
                <option>TRANSFER</option><option>CASH</option><option>CARD</option>
              </select>
            </label>
            <button className="btn primary" type="submit">Confirmar pago</button>
          </form>
        </section>

        <section className="card">
          <h3>Pagos pendientes ({pending.length})</h3>
          <table className="table">
            <thead><tr><th>ID</th><th>Estudiante</th><th>Concepto</th><th>Monto</th></tr></thead>
            <tbody>
              {pending.map((p) => (
                <tr key={p.paymentId}>
                  <td>{p.paymentId}</td><td>{p.studentId}</td><td>{p.concept}</td>
                  <td>{p.amount} {p.currency}</td>
                </tr>
              ))}
              {pending.length === 0 && <tr><td colSpan="4" className="muted">Sin pendientes</td></tr>}
            </tbody>
          </table>
        </section>
      </div>

      <section className="card">
        <h3>Pagos confirmados ({confirmed.length})</h3>
        <table className="table">
          <thead><tr><th>ID</th><th>Estudiante</th><th>Concepto</th><th>Monto</th><th>Método</th></tr></thead>
          <tbody>
            {confirmed.map((p) => (
              <tr key={p.paymentId}>
                <td>{p.paymentId}</td><td>{p.studentId}</td><td>{p.concept}</td>
                <td>{p.amount} {p.currency}</td><td>{p.method}</td>
              </tr>
            ))}
            {confirmed.length === 0 && <tr><td colSpan="5" className="muted">Sin pagos confirmados</td></tr>}
          </tbody>
        </table>
      </section>
    </div>
  )
}
