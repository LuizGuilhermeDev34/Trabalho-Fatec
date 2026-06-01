import axios from 'axios'

// Em produção (Docker), o Nginx faz proxy de /api → backend:8080
// Em desenvolvimento, o Vite proxy faz o mesmo para localhost:8080
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// Interceptor: injeta o JWT em toda requisição autenticada
api.interceptors.request.use(config => {
  const token = localStorage.getItem('castrapet_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Interceptor: trata erros globais
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('castrapet_token')
      localStorage.removeItem('castrapet_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// ─── Auth ───────────────────────────────
export const authService = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
}

// ─── Pets ───────────────────────────────
export const petService = {
  listar: () => api.get('/pets'),
  buscar: (id) => api.get(`/pets/${id}`),
  cadastrar: (data) => api.post('/pets', data),
  atualizar: (id, data) => api.put(`/pets/${id}`, data),
  remover: (id) => api.delete(`/pets/${id}`),
}

// ─── Clínicas ───────────────────────────
export const clinicaService = {
  listar: () => api.get('/clinicas'),
  buscarVagas: (clinicaId, data) =>
    api.get(`/clinicas/${clinicaId}/vagas`, { params: { data } }),
}

// ─── Agendamentos ───────────────────────
export const agendamentoService = {
  listar: () => api.get('/agendamentos'),
  buscar: (id) => api.get(`/agendamentos/${id}`),
  criar: (data) => api.post('/agendamentos', data),
  cancelar: (id) => api.put(`/agendamentos/${id}/cancelar`),
  atualizarStatus: (id, data) => api.put(`/agendamentos/${id}/status`, data),

  // Admin
  listarTodos: (page = 0, size = 20) =>
    api.get('/agendamentos/admin/todos', { params: { page, size } }),
  estatisticas: () => api.get('/agendamentos/admin/estatisticas'),
}

export default api
