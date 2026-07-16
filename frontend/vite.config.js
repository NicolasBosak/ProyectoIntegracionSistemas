import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Proxy de desarrollo: el navegador habla solo con http://localhost:3000 y Vite reenvia
// TODO al API Gateway (Paso 8), que valida el JWT y enruta a cada microservicio.
const GATEWAY = 'http://localhost:8080'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/auth': GATEWAY,
      '/academic': GATEWAY,
      '/payments': GATEWAY,
      '/notifications': GATEWAY,
      '/attendance': GATEWAY,
      '/analytics': GATEWAY
    }
  }
})
