import { useEffect, useState } from 'react'
import {
    Box, Button, Typography, Grid, Card, CardContent, Dialog,
    DialogTitle, DialogContent, DialogActions, TextField, MenuItem,
    Snackbar, Alert, Chip, CircularProgress, Divider, Stepper,
    Step, StepLabel, Checkbox, FormControlLabel, FormGroup,
    FormHelperText, Paper
} from '@mui/material'
import AddIcon from '@mui/icons-material/Add'
import CancelIcon from '@mui/icons-material/Cancel'
import CheckCircleIcon from '@mui/icons-material/CheckCircle'
import { getReservas, crearReserva, cancelarRes } from '../api/reservas'
import { getExperiencias } from '../api/experiencias'
import { getClientes } from '../api/clientes'
import { getImagenPorTipo } from '../utils/imagenes'

const METODOS_PAGO = [
    { value: 'EFECTIVO',        label: 'Efectivo' },
    { value: 'TARJETA_CREDITO', label: 'Tarjeta Crédito' },
    { value: 'TARJETA_DEBITO',  label: 'Tarjeta Débito' },
    { value: 'TRANSFERENCIA',   label: 'Transferencia' },
]

const PASOS = ['Seleccionar cliente', 'Elegir experiencias', 'Detalles y pago']

const FORM_VACIO = {
    clienteId:       '',
    experienciaIds:  [],
    fechaExperiencia:'',
    horaExperiencia: '',
    cantidadPersonas:'',
    metodoPago:      '',
    observaciones:   '',
}

const COLOR_ESTADO = {
    CONFIRMADA:  'success',
    PENDIENTE:   'warning',
    CANCELADA:   'error',
    COMPLETADA:  'info',
}

function ReservasPage() {
    const [reservas, setReservas]         = useState([])
    const [experiencias, setExperiencias] = useState([])
    const [clientes, setClientes]         = useState([])
    const [cargando, setCargando]         = useState(true)
    const [modalAbierto, setModalAbierto] = useState(false)
    const [pasoActual, setPasoActual]     = useState(0)
    const [form, setForm]                 = useState(FORM_VACIO)
    const [errores, setErrores]           = useState({})
    const [confirmCancel, setConfirmCancel] = useState(null)
    const [snack, setSnack]               = useState({ open: false, msg: '', tipo: 'success' })
    const [resumenDialog, setResumenDialog] = useState(null)

    // ── Carga inicial ────────────────────────────────────────────
    useEffect(() => {
        cargarDatos()
    }, [])

    const cargarDatos = async () => {
        try {
            setCargando(true)
            const [resR, resE, resC] = await Promise.all([
                getReservas(),
                getExperiencias(),
                getClientes(),
            ])
            setReservas(resR.data)
            setExperiencias(resE.data)
            setClientes(resC.data)
        } catch {
            mostrarSnack('Error al cargar datos', 'error')
        } finally {
            setCargando(false)
        }
    }

    // ── Snackbar ─────────────────────────────────────────────────
    const mostrarSnack = (msg, tipo = 'success') =>
        setSnack({ open: true, msg, tipo })

    // ── Modal ────────────────────────────────────────────────────
    const abrirModal = () => {
        setForm(FORM_VACIO)
        setErrores({})
        setPasoActual(0)
        setModalAbierto(true)
    }

    const cerrarModal = () => {
        setModalAbierto(false)
        setForm(FORM_VACIO)
        setErrores({})
        setPasoActual(0)
    }

    // ── Validación por paso ──────────────────────────────────────
    const validarPaso = (paso) => {
        const e = {}
        if (paso === 0) {
            if (!form.clienteId)
                e.clienteId = 'Debe seleccionar un cliente'
        }
        if (paso === 1) {
            if (form.experienciaIds.length === 0)
                e.experienciaIds = 'Debe seleccionar al menos una experiencia'
        }
        if (paso === 2) {
            if (!form.fechaExperiencia)
                e.fechaExperiencia = 'La fecha es obligatoria'
            if (!form.horaExperiencia)
                e.horaExperiencia = 'La hora es obligatoria'
            if (!form.cantidadPersonas || Number(form.cantidadPersonas) < 1)
                e.cantidadPersonas = 'Mínimo 1 persona'
            if (!form.metodoPago)
                e.metodoPago = 'Seleccione un método de pago'
        }
        setErrores(e)
        return Object.keys(e).length === 0
    }

    const siguientePaso = () => {
        if (validarPaso(pasoActual)) setPasoActual((p) => p + 1)
    }

    const pasoAnterior = () => {
        setErrores({})
        setPasoActual((p) => p - 1)
    }

    // ── Manejo de experiencias seleccionadas ─────────────────────
    const toggleExperiencia = (id) => {
        setForm((prev) => ({
            ...prev,
            experienciaIds: prev.experienciaIds.includes(id)
                ? prev.experienciaIds.filter((e) => e !== id)
                : [...prev.experienciaIds, id],
        }))
    }

    // ── Calcular total estimado ───────────────────────────────────
    const calcularTotal = () => {
        const seleccionadas = experiencias.filter((e) =>
            form.experienciaIds.includes(e.experienciaId))
        const suma = seleccionadas.reduce((acc, e) => acc + Number(e.precio), 0)
        return suma * (Number(form.cantidadPersonas) || 1)
    }

    // ── Confirmar reserva ────────────────────────────────────────
    const confirmarReserva = async () => {
        if (!validarPaso(2)) return
        const payload = {
            ...form,
            cantidadPersonas: Number(form.cantidadPersonas),
        }
        try {
            const { data } = await crearReserva(payload)
            cerrarModal()
            mostrarSnack('Reserva creada correctamente')
            setResumenDialog(data)
            cargarDatos()
        } catch (err) {
            const msg = err.response?.data?.message || 'Error al crear la reserva'
            mostrarSnack(msg, 'error')
        }
    }

    // ── Cancelar reserva ─────────────────────────────────────────
    const cancelarReserva = async () => {
        try {
            await cancelarRes(confirmCancel)
            mostrarSnack('Reserva cancelada')
            setConfirmCancel(null)
            cargarDatos()
        } catch {
            mostrarSnack('Error al cancelar la reserva', 'error')
        }
    }

    // ── Render paso 0: Seleccionar cliente ───────────────────────
    const renderPaso0 = () => (
        <Box sx={{ mt: 1 }}>
            <TextField
                select
                label="Cliente"
                value={form.clienteId}
                onChange={(e) => setForm({ ...form, clienteId: e.target.value })}
                error={!!errores.clienteId}
                helperText={errores.clienteId}
                fullWidth
            >
                {clientes.map((c) => (
                    <MenuItem key={c.clienteId} value={c.clienteId}>
                        {c.nombre} — {c.correo}
                    </MenuItem>
                ))}
            </TextField>
        </Box>
    )

    // ── Render paso 1: Seleccionar experiencias ──────────────────
    const renderPaso1 = () => (
        <Box sx={{ mt: 1 }}>
            <FormGroup>
                <Grid container spacing={2}>
                    {experiencias.map((exp) => {
                        const seleccionada = form.experienciaIds.includes(exp.experienciaId)
                        return (
                            <Grid item xs={12} sm={6} key={exp.experienciaId}>
                                <Paper
                                    variant="outlined"
                                    sx={{
                                        p: 1.5,
                                        borderRadius: 2,
                                        borderColor: seleccionada ? 'primary.main' : 'divider',
                                        borderWidth: seleccionada ? 2 : 1,
                                        cursor: 'pointer',
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: 1.5,
                                        transition: 'all 0.2s',
                                    }}
                                    onClick={() => toggleExperiencia(exp.experienciaId)}
                                >
                                    <Box
                                        component="img"
                                        src={getImagenPorTipo(exp.tipoExperiencia)}
                                        alt={exp.nombre}
                                        sx={{ width: 60, height: 60, borderRadius: 1, objectFit: 'cover' }}
                                    />
                                    <Box sx={{ flexGrow: 1 }}>
                                        <Typography variant="body2" fontWeight="bold">
                                            {exp.nombre}
                                        </Typography>
                                        <Typography variant="caption" color="text.secondary">
                                            ${Number(exp.precio).toLocaleString('es-CO')} COP · {exp.duracion}h
                                        </Typography>
                                    </Box>
                                    <Checkbox
                                        checked={seleccionada}
                                        color="primary"
                                        onClick={(e) => e.stopPropagation()}
                                        onChange={() => toggleExperiencia(exp.experienciaId)}
                                    />
                                </Paper>
                            </Grid>
                        )
                    })}
                </Grid>
            </FormGroup>
            {errores.experienciaIds && (
                <FormHelperText error sx={{ mt: 1 }}>
                    {errores.experienciaIds}
                </FormHelperText>
            )}
        </Box>
    )

    // ── Render paso 2: Detalles y pago ───────────────────────────
    const renderPaso2 = () => (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Box sx={{ display: 'flex', gap: 2 }}>
                <TextField
                    label="Fecha"
                    type="date"
                    value={form.fechaExperiencia}
                    onChange={(e) => setForm({ ...form, fechaExperiencia: e.target.value })}
                    error={!!errores.fechaExperiencia}
                    helperText={errores.fechaExperiencia}
                    InputLabelProps={{ shrink: true }}
                    fullWidth
                />
                <TextField
                    label="Hora"
                    type="time"
                    value={form.horaExperiencia}
                    onChange={(e) => setForm({ ...form, horaExperiencia: e.target.value })}
                    error={!!errores.horaExperiencia}
                    helperText={errores.horaExperiencia}
                    InputLabelProps={{ shrink: true }}
                    fullWidth
                />
            </Box>
            <Box sx={{ display: 'flex', gap: 2 }}>
                <TextField
                    label="Cantidad de personas"
                    type="number"
                    value={form.cantidadPersonas}
                    onChange={(e) => setForm({ ...form, cantidadPersonas: e.target.value })}
                    error={!!errores.cantidadPersonas}
                    helperText={errores.cantidadPersonas}
                    fullWidth
                />
                <TextField
                    select
                    label="Método de pago"
                    value={form.metodoPago}
                    onChange={(e) => setForm({ ...form, metodoPago: e.target.value })}
                    error={!!errores.metodoPago}
                    helperText={errores.metodoPago}
                    fullWidth
                >
                    {METODOS_PAGO.map((m) => (
                        <MenuItem key={m.value} value={m.value}>{m.label}</MenuItem>
                    ))}
                </TextField>
            </Box>
            <TextField
                label="Observaciones (opcional)"
                value={form.observaciones}
                onChange={(e) => setForm({ ...form, observaciones: e.target.value })}
                multiline
                rows={2}
                fullWidth
            />

            {/* Resumen de total estimado */}
            <Paper variant="outlined" sx={{ p: 2, borderRadius: 2, bgcolor: 'primary.50' }}>
                <Typography variant="subtitle2" color="text.secondary">
                    Total estimado
                </Typography>
                <Typography variant="h5" fontWeight="bold" color="primary">
                    ${calcularTotal().toLocaleString('es-CO')} COP
                </Typography>
                <Typography variant="caption" color="text.secondary">
                    {form.experienciaIds.length} experiencia(s) ×{' '}
                    {form.cantidadPersonas || 1} persona(s)
                </Typography>
            </Paper>
        </Box>
    )

    // ── Render principal ─────────────────────────────────────────
    return (
        <Box>
            {/* Encabezado */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" fontWeight="bold" color="primary">
                    Reservas
                </Typography>
                <Button variant="contained" startIcon={<AddIcon />} onClick={abrirModal}>
                    Nueva Reserva
                </Button>
            </Box>

            {/* Lista de reservas */}
            {cargando ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
                    <CircularProgress color="primary" />
                </Box>
            ) : reservas.length === 0 ? (
                <Paper sx={{ p: 6, textAlign: 'center', borderRadius: 3 }}>
                    <Typography color="text.secondary">
                        No hay reservas registradas aún.
                    </Typography>
                </Paper>
            ) : (
                <Grid container spacing={3}>
                    {reservas.map((r) => (
                        <Grid item xs={12} md={6} key={r.idReserva}>
                            <Card sx={{ borderRadius: 3, boxShadow: 2 }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                        <Typography variant="h6" fontWeight="bold">
                                            Reserva #{r.idReserva}
                                        </Typography>
                                        <Chip
                                            label={r.estadoReserva}
                                            color={COLOR_ESTADO[r.estadoReserva] || 'default'}
                                            size="small"
                                        />
                                    </Box>
                                    <Divider sx={{ mb: 1.5 }} />
                                    <Typography variant="body2">
                                        👤 <b>Cliente:</b> {r.clienteNombre}
                                    </Typography>
                                    <Typography variant="body2">
                                        🗓 <b>Fecha:</b> {r.fechaExperiencia} a las {r.horaExperiencia}
                                    </Typography>
                                    <Typography variant="body2">
                                        👥 <b>Personas:</b> {r.cantidadPersonas}
                                    </Typography>
                                    <Typography variant="body2">
                                        🎯 <b>Experiencias:</b> {r.experienciasNombres.join(', ')}
                                    </Typography>
                                    <Typography variant="body2">
                                        💳 <b>Pago:</b> {r.metodoPago}
                                    </Typography>
                                    <Typography variant="subtitle1" fontWeight="bold" color="primary" mt={1}>
                                        Total: ${Number(r.totalPagar).toLocaleString('es-CO')} COP
                                    </Typography>
                                    {r.observaciones && (
                                        <Typography variant="body2" color="text.secondary" mt={0.5}>
                                            📝 {r.observaciones}
                                        </Typography>
                                    )}
                                    {r.estadoReserva !== 'CANCELADA' && (
                                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                                            <Button
                                                variant="outlined"
                                                color="error"
                                                size="small"
                                                startIcon={<CancelIcon />}
                                                onClick={() => setConfirmCancel(r.idReserva)}
                                            >
                                                Cancelar reserva
                                            </Button>
                                        </Box>
                                    )}
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}

            {/* Modal nueva reserva con Stepper */}
            <Dialog open={modalAbierto} onClose={cerrarModal} maxWidth="sm" fullWidth>
                <DialogTitle fontWeight="bold">Nueva Reserva</DialogTitle>
                <DialogContent>
                    <Stepper activeStep={pasoActual} sx={{ mb: 3 }}>
                        {PASOS.map((label) => (
                            <Step key={label}>
                                <StepLabel>{label}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                    {pasoActual === 0 && renderPaso0()}
                    {pasoActual === 1 && renderPaso1()}
                    {pasoActual === 2 && renderPaso2()}
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={cerrarModal} color="inherit">Cancelar</Button>
                    {pasoActual > 0 && (
                        <Button onClick={pasoAnterior} color="inherit">Atrás</Button>
                    )}
                    {pasoActual < PASOS.length - 1 ? (
                        <Button onClick={siguientePaso} variant="contained">
                            Siguiente
                        </Button>
                    ) : (
                        <Button onClick={confirmarReserva} variant="contained" color="success">
                            Confirmar reserva
                        </Button>
                    )}
                </DialogActions>
            </Dialog>

            {/* Resumen reserva confirmada */}
            <Dialog open={!!resumenDialog} onClose={() => setResumenDialog(null)} maxWidth="xs" fullWidth>
                <DialogTitle sx={{ textAlign: 'center' }}>
                    <CheckCircleIcon color="success" sx={{ fontSize: 48 }} />
                    <Typography variant="h6" fontWeight="bold">
                        ¡Reserva confirmada!
                    </Typography>
                </DialogTitle>
                <DialogContent>
                    {resumenDialog && (
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                            <Typography><b>Reserva #:</b> {resumenDialog.idReserva}</Typography>
                            <Typography><b>Cliente:</b> {resumenDialog.clienteNombre}</Typography>
                            <Typography><b>Experiencias:</b> {resumenDialog.experienciasNombres.join(', ')}</Typography>
                            <Typography><b>Fecha:</b> {resumenDialog.fechaExperiencia}</Typography>
                            <Typography><b>Hora:</b> {resumenDialog.horaExperiencia}</Typography>
                            <Typography><b>Personas:</b> {resumenDialog.cantidadPersonas}</Typography>
                            <Typography><b>Método de pago:</b> {resumenDialog.metodoPago}</Typography>
                            <Divider />
                            <Typography variant="h6" color="primary" fontWeight="bold">
                                Total: ${Number(resumenDialog.totalPagar).toLocaleString('es-CO')} COP
                            </Typography>
                        </Box>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setResumenDialog(null)} variant="contained" fullWidth>
                        Cerrar
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Confirmación cancelar */}
            <Dialog open={!!confirmCancel} onClose={() => setConfirmCancel(null)}>
                <DialogTitle fontWeight="bold">¿Cancelar esta reserva?</DialogTitle>
                <DialogContent>
                    <Typography>Esta acción no se puede deshacer.</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setConfirmCancel(null)} color="inherit">Volver</Button>
                    <Button onClick={cancelarReserva} color="error" variant="contained">
                        Sí, cancelar
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

export default ReservasPage