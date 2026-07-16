import { useNavigate } from 'react-router-dom'
import { ROLES, setRole } from '../session.js'

// Login por rol (Paso 7). En el Paso 8 se reemplaza por /auth/login con usuario, clave y JWT.
export default function Login() {
  const navigate = useNavigate()

  function enter(role) {
    setRole(role)
    navigate(ROLES[role].path)
  }

  return (
    <div className="login">
      <div className="login-card">
        <h1>CampusConnect <span>360</span></h1>
        <p className="muted">Ecosistema de integración para una red de colegios</p>
        <p className="login-hint">Selecciona tu rol para ingresar</p>
        <div className="role-grid">
          {Object.entries(ROLES).map(([role, info]) => (
            <button key={role} className="role-card" onClick={() => enter(role)}>
              <strong>{info.label}</strong>
              <span className="muted">{role}</span>
            </button>
          ))}
        </div>
        <p className="login-note">
          El inicio de sesión con JWT se habilita en el Paso 8 (API Gateway + seguridad).
        </p>
      </div>
    </div>
  )
}
