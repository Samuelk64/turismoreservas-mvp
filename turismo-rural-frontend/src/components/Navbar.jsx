import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material'
import { useNavigate, useLocation } from 'react-router-dom'
import NatureIcon from '@mui/icons-material/Nature'

function Navbar() {
    const navigate  = useNavigate()
    const location  = useLocation()

    const esActivo = (ruta) => location.pathname === ruta

    return (
        <AppBar position="static" color="primary" elevation={2}>
            <Toolbar>
                <NatureIcon sx={{ mr: 1 }} />
                <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 'bold' }}>
                    Turismo Rural
                </Typography>
                <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button
                        color="inherit"
                        onClick={() => navigate('/experiencias')}
                        sx={{
                            fontWeight: esActivo('/experiencias') ? 'bold' : 'normal',
                            borderBottom: esActivo('/experiencias') ? '2px solid white' : 'none',
                            borderRadius: 0,
                        }}
                    >
                        Experiencias
                    </Button>
                    <Button
                        color="inherit"
                        onClick={() => navigate('/reservas')}
                        sx={{
                            fontWeight: esActivo('/reservas') ? 'bold' : 'normal',
                            borderBottom: esActivo('/reservas') ? '2px solid white' : 'none',
                            borderRadius: 0,
                        }}
                    >
                        Reservas
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    )
}

export default Navbar