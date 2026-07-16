import { NavLink, useNavigate } from 'react-router-dom'
import { getRole, clearRole, ROLES } from '../session.js'

// Para la demo "un dia de operacion" el navegador puede recorrer todos los portales.
// El rol activo se resalta; en el Paso 8 el JWT restringira el acceso por rol.
const NAV = [
  { to: '/academico', label: 'Portal Académico' },
  { to: '/finanzas', label: 'Portal Financiero' },
  { to: '/docente', label: 'Portal Docente' },
  { to: '/dashboard', label: 'Dashboard' }
]

export default function Layout({ children }) {
  const navigate = useNavigate()
  const role = getRole()
  const roleLabel = role && ROLES[role] ? ROLES[role].label : role

  function logout() {
    clearRole()
    navigate('/login')
  }

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">CampusConnect <span>360</span></div>
        <nav className="nav">
          {NAV.map((item) => (
            <NavLink key={item.to} to={item.to}
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="session">
          <span className="role-badge">{roleLabel}</span>
          <button className="btn ghost" onClick={logout}>Salir</button>
        </div>
      </header>
      <main className="content">{children}</main>
    </div>
  )
}
