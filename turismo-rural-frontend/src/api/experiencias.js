import api from './axios'

export const getExperiencias    = ()        => api.get('/experiencias')
export const getExperiencia     = (id)      => api.get(`/experiencias/${id}`)
export const crearExperiencia   = (datos)   => api.post('/experiencias', datos)
export const actualizarExp      = (id, datos) => api.put(`/experiencias/${id}`, datos)
export const eliminarExp        = (id)      => api.delete(`/experiencias/${id}`)