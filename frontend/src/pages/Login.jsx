import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api.js'
import { ROLES, saveSession } from '../session.js'

const DEMO_USERS = [
  { username: 'secretaria', role: 'SECRETARIA' },
  { username: 'finanzas', role: 'FINANZAS' },
  { username: 'docente', role: 'DOCENTE' },
  { username: 'direccion', role: 'DIRECCION' }
]

export default function Login() {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('demo123')
  const [error, setError] = useState(null)

  async function submit(e) {
    e.preventDefault()
    setError(null)
    try {
      const res = await api.post('/auth/login', { username, password })
      saveSession(res.token, res.role)
      navigate(ROLES[res.role]?.path || '/dashboard')
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <div className="login">
      <div className="login-card">
        <h1>CampusConnect <span>360</span></h1>
        <p className="muted">Ecosistema de integración para una red de colegios</p>

        <form onSubmit={submit} className="form" style={{ marginTop: '1.5rem', textAlign: 'left' }}>
          <label>Usuario
            <input required value={username} onChange={(e) => setUsername(e.target.value)}
              placeholder="secretaria / finanzas / docente / direccion" />
          </label>
          <label>Contraseña
            <input type="password" required value={password}
              onChange={(e) => setPassword(e.target.value)} />
          </label>
          <button className="btn primary" type="submit">Ingresar</button>
        </form>

        {error && <div className="alert error" style={{ marginTop: '1rem' }}>{error}</div>}

        <div className="demo-users">
          <span className="muted">Usuarios de prueba (clave: demo123):</span>
          <div className="chip-row">
            {DEMO_USERS.map((u) => (
              <button key={u.username} className="chip" onClick={() => setUsername(u.username)}>
                {u.username} <em>{ROLES[u.role].label}</em>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
