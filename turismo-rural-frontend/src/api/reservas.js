import api from './axios'

export const getReservas   = ()       => api.get('/reservas')
export const crearReserva  = (datos)  => api.post('/reservas', datos)
export const cancelarRes   = (id)     => api.put(`/reservas/${id}/cancelar`)