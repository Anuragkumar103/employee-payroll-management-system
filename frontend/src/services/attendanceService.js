import api from './apiClient';

export const attendanceService = {
  checkIn: () => api.post('/attendance/check-in'),
  checkOut: () => api.post('/attendance/check-out'),
  history: (employeeId, page = 0, size = 10) =>
    api.get(`/attendance/history/${employeeId}?page=${page}&size=${size}`),
  monthlySummary: (employeeId, month, year) =>
    api.get(`/attendance/summary/${employeeId}?month=${month}&year=${year}`)
};
