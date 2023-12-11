/// <reference types="vitest" />
/// <reference types="vite/client" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 4567, // Change this to the desired port number
  },
  plugins: [react()],  
  test: {
    globals: true,
    environment: "jsdom",
    css: true,
  }
})

