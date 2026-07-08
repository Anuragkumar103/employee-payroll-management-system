import api from './apiClient';

export const employeeService = {
  getAll: (page = 0, size = 10) => api.get(`/employees?page=${page}&size=${size}`),
  getById: (id) => api.get(`/employees/${id}`),
  search: (keyword, page = 0, size = 10) =>
    api.get(`/employees/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`),
  create: (payload) => api.post('/employees', payload),
  update: (id, payload) => api.put(`/employees/${id}`, payload),
  remove: (id) => api.delete(`/employees/${id}`)
};
