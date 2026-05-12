import api from './axios'

export const getClientes = () => api.get('/clientes')