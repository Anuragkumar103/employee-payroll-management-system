import api from './apiClient';

export const leaveService = {
  apply: (payload) => api.post('/leaves/apply', payload),
  approve: (id) => api.put(`/leaves/${id}/approve`),
  reject: (id) => api.put(`/leaves/${id}/reject`),
  balance: (year) => api.get(`/leaves/balance${year ? `?year=${year}` : ''}`),
  myLeaves: (page = 0, size = 10) => api.get(`/leaves/my-leaves?page=${page}&size=${size}`),
  pending: (page = 0, size = 10) => api.get(`/leaves/pending?page=${page}&size=${size}`)
};
