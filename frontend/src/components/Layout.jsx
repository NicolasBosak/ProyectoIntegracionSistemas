import { NavLink, useNavigate } from 'react-router-dom'
import { getRole, clearSession, ROLES, ROLE_ROUTES } from '../session.js'

const ALL_NAV = {
  '/academico': 'Portal Académico',
  '/finanzas': 'Portal Financiero',
  '/docente': 'Portal Docente',
  '/dashboard': 'Dashboard'
}

export default function Layout({ children }) {
  const navigate = useNavigate()
  const role = getRole()
  const roleLabel = role && ROLES[role] ? ROLES[role].label : role
  // El gateway autoriza por rol; la navegación muestra solo lo permitido.
  const allowed = ROLE_ROUTES[role] || []

  function logout() {
    clearSession()
    navigate('/login')
  }

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">CampusConnect <span>360</span></div>
        <nav className="nav">
          {allowed.map((to) => (
            <NavLink key={to} to={to}
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>
              {ALL_NAV[to]}
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
