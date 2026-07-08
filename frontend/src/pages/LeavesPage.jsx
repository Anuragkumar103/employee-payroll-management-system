import React, { useEffect, useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Alert, Button, Card, Table } from '../components/UI';
import { leaveService } from '../services/leaveService';
import { useAuth } from '../context/AuthContext';

export default function LeavesPage() {
  const { user } = useAuth();
  const canApprove = user?.role === 'ADMIN' || user?.role === 'HR';

  const [myLeaves, setMyLeaves] = useState([]);
  const [pendingLeaves, setPendingLeaves] = useState([]);
  const [balance, setBalance] = useState(null);
  const [form, setForm] = useState({ startDate: '', endDate: '', reason: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadMyLeaves = async () => {
    try {
      const { data } = await leaveService.myLeaves();
      setMyLeaves(data.data.content);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load your leave requests');
    }
  };

  const loadPending = async () => {
    if (!canApprove) return;
    try {
      const { data } = await leaveService.pending();
      setPendingLeaves(data.data.content);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load pending leave requests');
    }
  };

  const loadBalance = async () => {
    try {
      const { data } = await leaveService.balance();
      setBalance(data.data);
    } catch {
      // not linked to an employee profile — non-fatal
    }
  };

  useEffect(() => {
    loadMyLeaves();
    loadPending();
    loadBalance();
  }, []);

  const handleApply = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await leaveService.apply(form);
      setSuccess('Leave request submitted');
      setForm({ startDate: '', endDate: '', reason: '' });
      loadMyLeaves();
      loadBalance();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit leave request');
    }
  };

  const handleApprove = async (id) => {
    try {
      await leaveService.approve(id);
      loadPending();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to approve leave');
    }
  };

  const handleReject = async (id) => {
    try {
      await leaveService.reject(id);
      loadPending();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to reject leave');
    }
  };

  const myColumns = [
    { key: 'startDate', header: 'Start' },
    { key: 'endDate', header: 'End' },
    { key: 'totalDays', header: 'Days' },
    { key: 'reason', header: 'Reason' },
    { key: 'status', header: 'Status' }
  ];

  const pendingColumns = [
    { key: 'employeeName', header: 'Employee' },
    { key: 'startDate', header: 'Start' },
    { key: 'endDate', header: 'End' },
    { key: 'reason', header: 'Reason' },
    {
      key: 'actions',
      header: 'Actions',
      render: (r) => (
        <div className="flex gap-2">
          <button onClick={() => handleApprove(r.id)} className="text-emerald-600 hover:underline text-sm">
            Approve
          </button>
          <button onClick={() => handleReject(r.id)} className="text-red-500 hover:underline text-sm">
            Reject
          </button>
        </div>
      )
    }
  ];

  return (
    <AppLayout>
      <h1 className="text-2xl font-bold text-slate-800 mb-6">Leave Management</h1>

      <Alert type="error">{error}</Alert>
      <Alert type="success">{success}</Alert>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <Card title="Apply for Leave">
          <form onSubmit={handleApply} className="space-y-3">
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Start Date</label>
              <input
                type="date"
                required
                value={form.startDate}
                onChange={(e) => setForm({ ...form, startDate: e.target.value })}
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">End Date</label>
              <input
                type="date"
                required
                value={form.endDate}
                onChange={(e) => setForm({ ...form, endDate: e.target.value })}
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Reason</label>
              <textarea
                value={form.reason}
                onChange={(e) => setForm({ ...form, reason: e.target.value })}
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <Button type="submit">Submit Request</Button>
          </form>
        </Card>

        <Card title="Leave Balance">
          {balance ? (
            <div className="text-sm text-slate-700 space-y-1">
              <p>Year: {balance.year}</p>
              <p>Total Entitlement: {balance.totalEntitlement} days</p>
              <p>Taken: {balance.leavesTaken} days</p>
              <p className="font-semibold">Remaining: {balance.leavesRemaining} days</p>
            </div>
          ) : (
            <p className="text-slate-400 text-sm">No balance data available.</p>
          )}
        </Card>
      </div>

      <Card title="My Leave Requests" className="mb-6">
        <Table columns={myColumns} rows={myLeaves} />
      </Card>

      {canApprove && (
        <Card title="Pending Approvals">
          <Table columns={pendingColumns} rows={pendingLeaves} />
        </Card>
      )}
    </AppLayout>
  );
}
