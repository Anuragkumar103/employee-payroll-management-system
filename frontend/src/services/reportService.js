import api from './apiClient';

export const reportService = {
  departmentSalary: () => api.get('/reports/department-salary'),
  monthlyPayroll: (month, year) => api.get(`/reports/monthly-payroll?month=${month}&year=${year}`),
  attendance: (month, year) => api.get(`/reports/attendance?month=${month}&year=${year}`),
  topPaidEmployees: (limit = 10) => api.get(`/reports/top-paid-employees?limit=${limit}`)
};
