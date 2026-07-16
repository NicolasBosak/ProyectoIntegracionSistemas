// Sesion simple basada en rol (Paso 7). En el Paso 8 se reemplaza por login real con JWT.

const KEY = 'cc360_role'

export const ROLES = {
  SECRETARIA: { label: 'Secretaría Académica', path: '/academico' },
  FINANZAS: { label: 'Finanzas', path: '/finanzas' },
  DOCENTE: { label: 'Docente / Bienestar', path: '/docente' },
  DIRECCION: { label: 'Dirección', path: '/dashboard' }
}

export function getRole() {
  return localStorage.getItem(KEY)
}

export function setRole(role) {
  localStorage.setItem(KEY, role)
}

export function clearRole() {
  localStorage.removeItem(KEY)
}
