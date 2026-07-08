import React, { useEffect, useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Alert, Button, Card, Table } from '../components/UI';
import { payrollService } from '../services/payrollService';
import { useAuth } from '../context/AuthContext';

export default function PayrollPage() {
  const { user } = useAuth();
  const canGenerate = user?.role === 'ADMIN' || user?.role === 'HR';

  const [history, setHistory] = useState([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [employeeId, setEmployeeId] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadHistory = async () => {
    setError('');
    try {
      const { data } = await payrollService.myHistory();
      setHistory(data.data.content);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load payroll history');
    }
  };

  useEffect(() => {
    loadHistory();
  }, []);

  const handleGenerate = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      const payload = { month: Number(month), year: Number(year) };
      if (employeeId) payload.employeeId = Number(employeeId);
      const { data } = await payrollService.generate(payload);
      setSuccess(`Payroll generated for ${data.data.length} employee(s)`);
      loadHistory();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to generate payroll');
    }
  };

  const handleDownload = async (payrollId) => {
    try {
      const response = await payrollService.downloadPayslip(payrollId);
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `payslip-${payrollId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError('Failed to download payslip');
    }
  };

  const columns = [
    { key: 'payMonth', header: 'Month' },
    { key: 'payYear', header: 'Year' },
    { key: 'grossSalary', header: 'Gross' },
    { key: 'taxDeduction', header: 'Tax' },
    { key: 'netSalary', header: 'Net Salary' },
    { key: 'status', header: 'Status' },
    {
      key: 'actions',
      header: 'Payslip',
      render: (r) => (
        <button onClick={() => handleDownload(r.id)} className="text-brand-600 hover:underline text-sm">
          Download PDF
        </button>
      )
    }
  ];

  return (
    <AppLayout>
      <h1 className="text-2xl font-bold text-slate-800 mb-6">Payroll</h1>

      <Alert type="error">{error}</Alert>
      <Alert type="success">{success}</Alert>

      {canGenerate && (
        <Card title="Generate Payroll" className="mb-6">
          <form onSubmit={handleGenerate} className="flex flex-wrap gap-3 items-end">
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Month</label>
              <input
                type="number"
                min="1"
                max="12"
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
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Employee ID (optional)</label>
              <input
                type="number"
                value={employeeId}
                onChange={(e) => setEmployeeId(e.target.value)}
                placeholder="All active employees"
                className="w-56 border border-slate-300 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <Button type="submit">Generate</Button>
          </form>
        </Card>
      )}

      <Card title="Payroll History">
        <Table columns={columns} rows={history} />
      </Card>
    </AppLayout>
  );
}
