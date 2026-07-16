// Cliente HTTP minimo. Las rutas son relativas: en desarrollo Vite las reenvia a cada
// microservicio; en el Paso 8 pasaran por el API Gateway (mismo origen).

async function request(method, path, body) {
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' }
  }
  // El token JWT se agregara en el Paso 8:
  // const token = localStorage.getItem('cc360_token')
  // if (token) options.headers['Authorization'] = `Bearer ${token}`
  if (body !== undefined) options.body = JSON.stringify(body)

  const res = await fetch(path, options)
  const text = await res.text()
  const data = text ? JSON.parse(text) : null
  if (!res.ok) {
    const message = data && data.message ? data.message : `Error ${res.status}`
    throw new Error(message)
  }
  return data
}

export const api = {
  get: (path) => request('GET', path),
  post: (path, body) => request('POST', path, body)
}
