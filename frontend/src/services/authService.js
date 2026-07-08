import api from './apiClient';

export const authService = {
  login: (username, password) => api.post('/auth/login', { username, password }),
  register: (payload) => api.post('/auth/register', payload),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (token, newPassword) => api.post('/auth/reset-password', { token, newPassword }),
  logout: () => api.post('/auth/logout')
};
