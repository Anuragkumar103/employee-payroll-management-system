import React, { useEffect, useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Card } from '../components/UI';
import { useAuth } from '../context/AuthContext';
import { reportService } from '../services/reportService';
import { departmentService } from '../services/departmentService';

export default function DashboardPage() {
  const { user } = useAuth();
  const [departmentCount, setDepartmentCount] = useState(null);
  const [salaryReport, setSalaryReport] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await departmentService.getAll();
        setDepartmentCount(data.data.length);
      } catch {
        setDepartmentCount('-');
      }

      if (user?.role === 'ADMIN' || user?.role === 'HR') {
        try {
          const { data } = await reportService.departmentSalary();
          setSalaryReport(data.data);
        } catch (err) {
          setError(err.response?.data?.message || 'Could not load salary report');
        }
      }
    };
    load();
  }, [user]);

  return (
    <AppLayout>
      <h1 className="text-2xl font-bold text-slate-800 mb-1">Welcome, {user?.username}</h1>
      <p className="text-slate-500 mb-6">Here's what's happening across the organization.</p>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <Card title="Departments">
          <p className="text-3xl font-bold text-brand-600">{departmentCount ?? '...'}</p>
        </Card>
        <Card title="Your Role">
          <p className="text-3xl font-bold text-brand-600">{user?.role}</p>
        </Card>
        <Card title="Signed in as">
          <p className="text-lg font-semibold text-slate-700">{user?.email}</p>
        </Card>
      </div>

      {(user?.role === 'ADMIN' || user?.role === 'HR') && (
        <Card title="Department Salary Overview">
          {error && <p className="text-red-500 text-sm mb-3">{error}</p>}
          <table className="w-full text-sm text-left">
            <thead className="text-slate-500 uppercase text-xs">
              <tr>
                <th className="px-3 py-2">Department</th>
                <th className="px-3 py-2">Employees</th>
                <th className="px-3 py-2">Total Net Salary</th>
                <th className="px-3 py-2">Avg. Net Salary</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {salaryReport.map((row) => (
                <tr key={row.departmentName}>
                  <td className="px-3 py-2">{row.departmentName}</td>
                  <td className="px-3 py-2">{row.employeeCount}</td>
                  <td className="px-3 py-2">{row.totalNetSalary}</td>
                  <td className="px-3 py-2">{row.averageNetSalary}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </AppLayout>
  );
}
