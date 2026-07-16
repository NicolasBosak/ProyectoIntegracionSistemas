import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Proxy de desarrollo: el navegador habla solo con http://localhost:3000 y Vite reenvia
// cada ruta a su microservicio. En el Paso 8 este rol lo asume el API Gateway.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/academic': 'http://localhost:8091',
      '/payments': 'http://localhost:8092',
      '/notifications': 'http://localhost:8093',
      '/attendance': 'http://localhost:8094',
      '/analytics': 'http://localhost:8095'
    }
  }
})
