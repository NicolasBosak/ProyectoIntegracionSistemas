import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import Login from './pages/Login.jsx'
import AcademicPortal from './pages/AcademicPortal.jsx'
import FinancialPortal from './pages/FinancialPortal.jsx'
import TeacherPortal from './pages/TeacherPortal.jsx'
import Dashboard from './pages/Dashboard.jsx'
import { getToken, getRole, ROLES } from './session.js'

function Protected({ children }) {
  return getToken() ? <Layout>{children}</Layout> : <Navigate to="/login" replace />
}

function home() {
  const role = getRole()
  return role && ROLES[role] ? ROLES[role].path : '/login'
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/academico" element={<Protected><AcademicPortal /></Protected>} />
      <Route path="/finanzas" element={<Protected><FinancialPortal /></Protected>} />
      <Route path="/docente" element={<Protected><TeacherPortal /></Protected>} />
      <Route path="/dashboard" element={<Protected><Dashboard /></Protected>} />
      <Route path="*" element={<Navigate to={getToken() ? home() : '/login'} replace />} />
    </Routes>
  )
}
