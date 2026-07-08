import React, { useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Alert, Button, Card, Table } from '../components/UI';
import { attendanceService } from '../services/attendanceService';
import { useAuth } from '../context/AuthContext';

export default function AttendancePage() {
  const { user } = useAuth();
  const [history, setHistory] = useState([]);
  const [summary, setSummary] = useState(null);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const employeeId = user?.employeeId;

  const handleCheckIn = async () => {
    setError('');
    setSuccess('');
    try {
      await attendanceService.checkIn();
      setSuccess('Checked in successfully');
    } catch (err) {
      setError(err.response?.data?.message || 'Check-in failed');
    }
  };

  const handleCheckOut = async () => {
    setError('');
    setSuccess('');
    try {
      await attendanceService.checkOut();
      setSuccess('Checked out successfully');
    } catch (err) {
      setError(err.response?.data?.message || 'Check-out failed');
    }
  };

  const loadHistory = async () => {
    setError('');
    try {
      const { data } = await attendanceService.history(employeeId);
      setHistory(data.data.content);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load history (are you linked to an employee record?)');
    }
  };

  const loadSummary = async () => {
    setError('');
    try {
      const { data } = await attendanceService.monthlySummary(employeeId, month, year);
      setSummary(data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load summary');
    }
  };

  const columns = [
    { key: 'attendanceDate', header: 'Date' },
    { key: 'checkInTime', header: 'Check In' },
    { key: 'checkOutTime', header: 'Check Out' },
    { key: 'status', header: 'Status' }
  ];

  return (
    <AppLayout>
      <h1 className="text-2xl font-bold text-slate-800 mb-6">Attendance</h1>

      <Alert type="error">{error}</Alert>
      <Alert type="success">{success}</Alert>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <Card title="Mark Attendance">
          <div className="flex gap-3">
            <Button onClick={handleCheckIn}>Check In</Button>
            <Button onClick={handleCheckOut} variant="secondary">
              Check Out
            </Button>
          </div>
        </Card>

        <Card title="Monthly Summary">
          <div className="flex gap-2 mb-4">
            <input
              type="number"
              value={month}
              onChange={(e) => setMonth(Number(e.target.value))}
              className="w-20 border border-slate-300 rounded-lg px-2 py-1 text-sm"
              placeholder="Month"
            />
            <input
              type="number"
              value={year}
              onChange={(e) => setYear(Number(e.target.value))}
              className="w-24 border border-slate-300 rounded-lg px-2 py-1 text-sm"
              placeholder="Year"
            />
            <Button onClick={loadSummary} variant="secondary">
              Load
            </Button>
          </div>
          {summary && (
            <div className="text-sm text-slate-700 space-y-1">
              <p>Present: {summary.presentDays}</p>
              <p>Absent: {summary.absentDays}</p>
              <p>Half Days: {summary.halfDays}</p>
              <p>On Leave: {summary.onLeaveDays}</p>
              <p>Total Working Days: {summary.totalWorkingDays}</p>
            </div>
          )}
        </Card>
      </div>

      <Card title="Attendance History" actions={<Button onClick={loadHistory} variant="secondary">Refresh</Button>}>
        <Table columns={columns} rows={history} />
      </Card>
    </AppLayout>
  );
}
