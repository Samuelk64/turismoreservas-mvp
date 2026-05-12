import { Routes, Route, Navigate } from 'react-router-dom'
import { Box } from '@mui/material'
import Navbar from './components/Navbar'
import ExperienciasPage from './pages/ExperienciasPage'
import ReservasPage from './pages/ReservasPage'

function App() {
  return (
      <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
        <Navbar />
        <Box sx={{ maxWidth: 1200, mx: 'auto', px: 3, py: 4 }}>
          <Routes>
            <Route path="/" element={<Navigate to="/experiencias" replace />} />
            <Route path="/experiencias" element={<ExperienciasPage />} />
            <Route path="/reservas" element={<ReservasPage />} />
          </Routes>
        </Box>
      </Box>
  )
}

export default App
