import api from './apiClient';

export const departmentService = {
  getAll: () => api.get('/departments'),
  getById: (id) => api.get(`/departments/${id}`),
  create: (payload) => api.post('/departments', payload),
  update: (id, payload) => api.put(`/departments/${id}`, payload),
  remove: (id) => api.delete(`/departments/${id}`),
  getEmployees: (id, page = 0, size = 10) => api.get(`/departments/${id}/employees?page=${page}&size=${size}`)
};
