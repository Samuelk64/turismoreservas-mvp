import { useEffect, useState } from 'react'
import {
    Box, Button, Typography, Grid, Card, CardMedia, CardContent,
    CardActions, Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, MenuItem, IconButton, Snackbar, Alert, Chip,
    CircularProgress
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import { getExperiencias, crearExperiencia, actualizarExp, eliminarExp } from '../api/experiencias'
import { getImagenPorTipo } from '../utils/Imagenes'

const TIPOS = ['Senderismo', 'Naturaleza', 'Gastronomia', 'Cultural']

const FORM_VACIO = {
    nombre:          '',
    descripcion:     '',
    precio:          '',
    duracion:        '',
    ubicacion:       '',
    tipoExperiencia: '',
    capacidadMaxima: '',
}

function ExperienciasPage() {
    const [experiencias, setExperiencias]   = useState([])
    const [cargando, setCargando]           = useState(true)
    const [modalAbierto, setModalAbierto]   = useState(false)
    const [editando, setEditando]           = useState(null)   // null = crear, obj = editar
    const [form, setForm]                   = useState(FORM_VACIO)
    const [errores, setErrores]             = useState({})
    const [confirmElim, setConfirmElim]     = useState(null)   // id a eliminar
    const [snack, setSnack]                 = useState({ open: false, msg: '', tipo: 'success' })

    // ── Cargar experiencias ──────────────────────────────────────
    useEffect(() => {
        cargarExperiencias()
    }, [])

    const cargarExperiencias = async () => {
        try {
            setCargando(true)
            const { data } = await getExperiencias()
            setExperiencias(data)
        } catch {
            mostrarSnack('Error al cargar experiencias', 'error')
        } finally {
            setCargando(false)
        }
    }

    // ── Snackbar ─────────────────────────────────────────────────
    const mostrarSnack = (msg, tipo = 'success') =>
        setSnack({ open: true, msg, tipo })

    // ── Modal ────────────────────────────────────────────────────
    const abrirCrear = () => {
        setEditando(null)
        setForm(FORM_VACIO)
        setErrores({})
        setModalAbierto(true)
    }

    const abrirEditar = (exp) => {
        setEditando(exp)
        setForm({
            nombre:          exp.nombre,
            descripcion:     exp.descripcion,
            precio:          exp.precio,
            duracion:        exp.duracion,
            ubicacion:       exp.ubicacion,
            tipoExperiencia: exp.tipoExperiencia,
            capacidadMaxima: exp.capacidadMaxima,
        })
        setErrores({})
        setModalAbierto(true)
    }

    const cerrarModal = () => {
        setModalAbierto(false)
        setEditando(null)
        setForm(FORM_VACIO)
        setErrores({})
    }

    // ── Validación ───────────────────────────────────────────────
    const validar = () => {
        const e = {}
        if (!form.nombre.trim())
            e.nombre = 'El nombre es obligatorio'
        if (!form.descripcion.trim())
            e.descripcion = 'La descripción es obligatoria'
        if (!form.precio || Number(form.precio) <= 0)
            e.precio = 'El precio debe ser mayor a 0'
        if (!form.duracion || Number(form.duracion) < 1)
            e.duracion = 'La duración mínima es 1 hora'
        if (!form.ubicacion.trim())
            e.ubicacion = 'La ubicación es obligatoria'
        if (!form.tipoExperiencia)
            e.tipoExperiencia = 'El tipo es obligatorio'
        if (!form.capacidadMaxima || Number(form.capacidadMaxima) < 1)
            e.capacidadMaxima = 'La capacidad mínima es 1'
        setErrores(e)
        return Object.keys(e).length === 0
    }

    // ── Guardar (crear o editar) ──────────────────────────────────
    const guardar = async () => {
        if (!validar()) return
        const payload = {
            ...form,
            precio:          Number(form.precio),
            duracion:        Number(form.duracion),
            capacidadMaxima: Number(form.capacidadMaxima),
        }
        try {
            if (editando) {
                await actualizarExp(editando.experienciaId, payload)
                mostrarSnack('Experiencia actualizada correctamente')
            } else {
                await crearExperiencia(payload)
                mostrarSnack('Experiencia creada correctamente')
            }
            cerrarModal()
            cargarExperiencias()
        } catch {
            mostrarSnack('Error al guardar la experiencia', 'error')
        }
    }

    // ── Eliminar ─────────────────────────────────────────────────
    const confirmarEliminar = async () => {
        try {
            await eliminarExp(confirmElim)
            mostrarSnack('Experiencia eliminada correctamente')
            setConfirmElim(null)
            cargarExperiencias()
        } catch {
            mostrarSnack('Error al eliminar la experiencia', 'error')
        }
    }

    // ── Render ───────────────────────────────────────────────────
    return (
        <Box>
            {/* Encabezado */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" fontWeight="bold" color="primary">
                    Experiencias
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={abrirCrear}
                >
                    Nueva Experiencia
                </Button>
            </Box>

            {/* Tarjetas */}
            {cargando ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
                    <CircularProgress color="primary" />
                </Box>
            ) : (
                <Grid container spacing={3}>
                    {experiencias.map((exp) => (
                        <Grid item xs={12} sm={6} md={4} key={exp.experienciaId}>
                            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', borderRadius: 3, boxShadow: 3 }}>
                                <CardMedia
                                    component="img"
                                    height="180"
                                    image={getImagenPorTipo(exp.tipoExperiencia)}
                                    alt={exp.nombre}
                                    sx={{ objectFit: 'cover' }}
                                />
                                <CardContent sx={{ flexGrow: 1 }}>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                        <Typography variant="h6" fontWeight="bold">
                                            {exp.nombre}
                                        </Typography>
                                        <Chip
                                            label={exp.tipoExperiencia}
                                            color="primary"
                                            size="small"
                                        />
                                    </Box>
                                    <Typography variant="body2" color="text.secondary" mb={1}>
                                        {exp.descripcion}
                                    </Typography>
                                    <Typography variant="body2">📍 {exp.ubicacion}</Typography>
                                    <Typography variant="body2">⏱ {exp.duracion} horas</Typography>
                                    <Typography variant="body2">👥 Capacidad: {exp.capacidadMaxima} personas</Typography>
                                    <Typography variant="subtitle1" fontWeight="bold" color="primary" mt={1}>
                                        ${Number(exp.precio).toLocaleString('es-CO')} COP
                                    </Typography>
                                </CardContent>
                                <CardActions sx={{ justifyContent: 'flex-end', px: 2, pb: 2 }}>
                                    <IconButton color="primary" onClick={() => abrirEditar(exp)}>
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton color="error" onClick={() => setConfirmElim(exp.experienciaId)}>
                                        <DeleteIcon />
                                    </IconButton>
                                </CardActions>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}

            {/* Modal Crear / Editar */}
            <Dialog open={modalAbierto} onClose={cerrarModal} maxWidth="sm" fullWidth>
                <DialogTitle fontWeight="bold">
                    {editando ? 'Editar Experiencia' : 'Nueva Experiencia'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <TextField
                            label="Nombre"
                            value={form.nombre}
                            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                            error={!!errores.nombre}
                            helperText={errores.nombre}
                            fullWidth
                        />
                        <TextField
                            label="Descripción"
                            value={form.descripcion}
                            onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                            error={!!errores.descripcion}
                            helperText={errores.descripcion}
                            multiline
                            rows={3}
                            fullWidth
                        />
                        <Box sx={{ display: 'flex', gap: 2 }}>
                            <TextField
                                label="Precio (COP)"
                                type="number"
                                value={form.precio}
                                onChange={(e) => setForm({ ...form, precio: e.target.value })}
                                error={!!errores.precio}
                                helperText={errores.precio}
                                fullWidth
                            />
                            <TextField
                                label="Duración (horas)"
                                type="number"
                                value={form.duracion}
                                onChange={(e) => setForm({ ...form, duracion: e.target.value })}
                                error={!!errores.duracion}
                                helperText={errores.duracion}
                                fullWidth
                            />
                        </Box>
                        <TextField
                            label="Ubicación"
                            value={form.ubicacion}
                            onChange={(e) => setForm({ ...form, ubicacion: e.target.value })}
                            error={!!errores.ubicacion}
                            helperText={errores.ubicacion}
                            fullWidth
                        />
                        <Box sx={{ display: 'flex', gap: 2 }}>
                            <TextField
                                select
                                label="Tipo de Experiencia"
                                value={form.tipoExperiencia}
                                onChange={(e) => setForm({ ...form, tipoExperiencia: e.target.value })}
                                error={!!errores.tipoExperiencia}
                                helperText={errores.tipoExperiencia}
                                fullWidth
                            >
                                {TIPOS.map((t) => (
                                    <MenuItem key={t} value={t}>{t}</MenuItem>
                                ))}
                            </TextField>
                            <TextField
                                label="Capacidad máxima"
                                type="number"
                                value={form.capacidadMaxima}
                                onChange={(e) => setForm({ ...form, capacidadMaxima: e.target.value })}
                                error={!!errores.capacidadMaxima}
                                helperText={errores.capacidadMaxima}
                                fullWidth
                            />
                        </Box>
                    </Box>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={cerrarModal} color="inherit">Cancelar</Button>
                    <Button onClick={guardar} variant="contained">
                        {editando ? 'Guardar cambios' : 'Crear'}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Confirmación eliminar */}
            <Dialog open={!!confirmElim} onClose={() => setConfirmElim(null)}>
                <DialogTitle fontWeight="bold">¿Eliminar experiencia?</DialogTitle>
                <DialogContent>
                    <Typography>Esta acción no se puede deshacer.</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setConfirmElim(null)} color="inherit">Cancelar</Button>
                    <Button onClick={confirmarEliminar} color="error" variant="contained">
                        Eliminar
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Snackbar */}
            <Snackbar
                open={snack.open}
                autoHideDuration={3000}
                onClose={() => setSnack({ ...snack, open: false })}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert severity={snack.tipo} variant="filled">
                    {snack.msg}
                </Alert>
            </Snackbar>
        </Box>
    )
}

export default ExperienciasPage