import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import Login from './pages/Login.jsx'
import AcademicPortal from './pages/AcademicPortal.jsx'
import FinancialPortal from './pages/FinancialPortal.jsx'
import TeacherPortal from './pages/TeacherPortal.jsx'
import Dashboard from './pages/Dashboard.jsx'
import { getRole } from './session.js'

function Protected({ children }) {
  return getRole() ? <Layout>{children}</Layout> : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/academico" element={<Protected><AcademicPortal /></Protected>} />
      <Route path="/finanzas" element={<Protected><FinancialPortal /></Protected>} />
      <Route path="/docente" element={<Protected><TeacherPortal /></Protected>} />
      <Route path="/dashboard" element={<Protected><Dashboard /></Protected>} />
      <Route path="*" element={<Navigate to={getRole() ? '/dashboard' : '/login'} replace />} />
    </Routes>
  )
}
