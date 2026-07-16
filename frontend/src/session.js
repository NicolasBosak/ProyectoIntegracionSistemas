// Sesion basada en JWT (Paso 8). El token y el rol se guardan tras /auth/login.

const TOKEN_KEY = 'cc360_token'
const ROLE_KEY = 'cc360_role'

export const ROLES = {
  SECRETARIA: { label: 'Secretaría Académica', path: '/academico' },
  FINANZAS: { label: 'Finanzas', path: '/finanzas' },
  DOCENTE: { label: 'Docente / Bienestar', path: '/docente' },
  DIRECCION: { label: 'Dirección', path: '/dashboard' }
}

// Rutas del frontend permitidas por rol (coincide con la autorizacion del gateway).
export const ROLE_ROUTES = {
  SECRETARIA: ['/academico'],
  FINANZAS: ['/finanzas'],
  DOCENTE: ['/docente'],
  DIRECCION: ['/dashboard']
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY)
}

export function saveSession(token, role) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(ROLE_KEY, role)
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ROLE_KEY)
}
