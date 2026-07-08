import React, { useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Alert, Button, Card, Table } from '../components/UI';
import { reportService } from '../services/reportService';

export default function ReportsPage() {
  const [departmentSalary, setDepartmentSalary] = useState([]);
  const [monthlyPayroll, setMonthlyPayroll] = useState(null);
  const [attendance, setAttendance] = useState([]);
  const [topPaid, setTopPaid] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [error, setError] = useState('');

  const handleError = (err, fallback) => setError(err.response?.data?.message || fallback);

  const loadDepartmentSalary = async () => {
    try {
      const { data } = await reportService.departmentSalary();
      setDepartmentSalary(data.data);
    } catch (err) {
      handleError(err, 'Failed to load department salary report');
    }
  };

  const loadMonthlyPayroll = async () => {
    try {
      const { data } = await reportService.monthlyPayroll(month, year);
      setMonthlyPayroll(data.data);
    } catch (err) {
      handleError(err, 'Failed to load monthly payroll report');
    }
  };

  const loadAttendance = async () => {
    try {
      const { data } = await reportService.attendance(month, year);
      setAttendance(data.data);
    } catch (err) {
      handleError(err, 'Failed to load attendance report');
    }
  };

  const loadTopPaid = async () => {
    try {
      const { data } = await reportService.topPaidEmployees(10);
      setTopPaid(data.data);
    } catch (err) {
      handleError(err, 'Failed to load top paid employees report');
    }
  };

  const deptColumns = [
    { key: 'departmentName', header: 'Department' },
    { key: 'employeeCount', header: 'Employees' },
    { key: 'totalBasicSalary', header: 'Total Basic' },
    { key: 'totalNetSalary', header: 'Total Net' },
    { key: 'averageNetSalary', header: 'Avg Net' }
  ];

  const attendanceColumns = [
    { key: 'employeeName', header: 'Employee' },
    { key: 'presentDays', header: 'Present' },
    { key: 'absentDays', header: 'Absent' },
    { key: 'halfDays', header: 'Half Days' },
    { key: 'onLeaveDays', header: 'On Leave' }
  ];

  const topPaidColumns = [
    { key: 'employeeName', header: 'Employee' },
    { key: 'departmentName', header: 'Department' },
    { key: 'netSalary', header: 'Net Salary' }
  ];

  return (
    <AppLayout>
      <h1 className="text-2xl font-bold text-slate-800 mb-6">Reports</h1>

      <Alert type="error">{error}</Alert>

      <div className="flex gap-3 mb-6 items-end">
        <div>
          <label className="block text-sm font-medium text-slate-600 mb-1">Month</label>
          <input
            type="number"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
            className="w-24 border border-slate-300 rounded-lg px-3 py-2 text-sm"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-slate-600 mb-1">Year</label>
          <input
            type="number"
            value={year}
            onChange={(e) => setYear(e.target.value)}
            className="w-28 border border-slate-300 rounded-lg px-3 py-2 text-sm"
          />
        </div>
      </div>

      <div className="space-y-6">
        <Card title="Department Salary Report" actions={<Button variant="secondary" onClick={loadDepartmentSalary}>Load</Button>}>
          <Table columns={deptColumns} rows={departmentSalary} />
        </Card>

        <Card title="Monthly Payroll Report" actions={<Button variant="secondary" onClick={loadMonthlyPayroll}>Load</Button>}>
          {monthlyPayroll ? (
            <div className="text-sm text-slate-700 space-y-1">
              <p>Employees Paid: {monthlyPayroll.employeesPaid}</p>
              <p>Total Gross Salary: {monthlyPayroll.totalGrossSalary}</p>
              <p>Total Deductions: {monthlyPayroll.totalDeductions}</p>
              <p className="font-semibold">Total Net Salary: {monthlyPayroll.totalNetSalary}</p>
            </div>
          ) : (
            <p className="text-slate-400 text-sm">Click "Load" to fetch this report.</p>
          )}
        </Card>

        <Card title="Attendance Report" actions={<Button variant="secondary" onClick={loadAttendance}>Load</Button>}>
          <Table columns={attendanceColumns} rows={attendance} />
        </Card>

        <Card title="Top Paid Employees" actions={<Button variant="secondary" onClick={loadTopPaid}>Load</Button>}>
          <Table columns={topPaidColumns} rows={topPaid} />
        </Card>
      </div>
    </AppLayout>
  );
}
