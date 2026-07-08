import api from './apiClient';

export const payrollService = {
  generate: (payload) => api.post('/payroll/generate', payload),
  historyForEmployee: (employeeId, page = 0, size = 10) =>
    api.get(`/payroll/history/${employeeId}?page=${page}&size=${size}`),
  myHistory: (page = 0, size = 10) => api.get(`/payroll/my-history?page=${page}&size=${size}`),
  downloadPayslip: (payrollId) =>
    api.get(`/payroll/${payrollId}/payslip`, { responseType: 'blob' })
};
