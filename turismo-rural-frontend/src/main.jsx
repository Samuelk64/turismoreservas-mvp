import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import App from './App'

const theme = createTheme({
    palette: {
        primary: {
            main: '#2e7d32',  // verde bosque — acorde al turismo rural
        },
        secondary: {
            main: '#f9a825',  // amarillo cálido
        },
        background: {
            default: '#f5f5f0',
        },
    },
    typography: {
        fontFamily: '"Segoe UI", "Roboto", sans-serif',
    },
})

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <BrowserRouter>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <App />
            </ThemeProvider>
        </BrowserRouter>
    </React.StrictMode>
)
