import React, { useEffect, useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Alert, Button, Card, Table } from '../components/UI';
import { employeeService } from '../services/employeeService';
import { departmentService } from '../services/departmentService';

const EMPTY_FORM = {
  employeeCode: '',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  designation: '',
  departmentId: '',
  dateOfJoining: '',
  basicSalary: '',
  hra: '',
  allowances: '',
  address: ''
};

export default function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);

  const loadEmployees = async () => {
    try {
      const { data } = await employeeService.getAll(0, 50);
      setEmployees(data.data.content);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load employees');
    }
  };

  const loadDepartments = async () => {
    try {
      const { data } = await departmentService.getAll();
      setDepartments(data.data);
    } catch {
      // non-fatal for this page
    }
  };

  useEffect(() => {
    loadEmployees();
    loadDepartments();
  }, []);

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
    setShowForm(false);
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    const payload = {
      ...form,
      departmentId: Number(form.departmentId),
      basicSalary: Number(form.basicSalary),
      hra: Number(form.hra || 0),
      allowances: Number(form.allowances || 0)
    };

    try {
      if (editingId) {
        await employeeService.update(editingId, payload);
        setSuccess('Employee updated successfully');
      } else {
        await employeeService.create(payload);
        setSuccess('Employee added successfully');
      }
      resetForm();
      loadEmployees();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save employee');
    }
  };

  const handleEdit = (employee) => {
    setForm({
      employeeCode: employee.employeeCode,
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email,
      phone: employee.phone || '',
      designation: employee.designation || '',
      departmentId: employee.departmentId || '',
      dateOfJoining: employee.dateOfJoining || '',
      basicSalary: employee.basicSalary || '',
      hra: employee.hra || '',
      allowances: employee.allowances || '',
      address: employee.address || ''
    });
    setEditingId(employee.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Deactivate this employee?')) return;
    try {
      await employeeService.remove(id);
      loadEmployees();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete employee');
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!keyword.trim()) {
      loadEmployees();
      return;
    }
    try {
      const { data } = await employeeService.search(keyword);
      setEmployees(data.data.content);
    } catch (err) {
      setError(err.response?.data?.message || 'Search failed');
    }
  };

  const columns = [
    { key: 'employeeCode', header: 'Code' },
    { key: 'name', header: 'Name', render: (r) => `${r.firstName} ${r.lastName}` },
    { key: 'email', header: 'Email' },
    { key: 'departmentName', header: 'Department' },
    { key: 'designation', header: 'Designation' },
    { key: 'status', header: 'Status' },
    {
      key: 'actions',
      header: 'Actions',
      render: (r) => (
        <div className="flex gap-2">
          <button onClick={() => handleEdit(r)} className="text-brand-600 hover:underline text-sm">
            Edit
          </button>
          <button onClick={() => handleDelete(r.id)} className="text-red-500 hover:underline text-sm">
            Delete
          </button>
        </div>
      )
    }
  ];

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Employees</h1>
        <Button onClick={() => (showForm ? resetForm() : setShowForm(true))}>
          {showForm ? 'Cancel' : '+ Add Employee'}
        </Button>
      </div>

      <Alert type="error">{error}</Alert>
      <Alert type="success">{success}</Alert>

      {showForm && (
        <Card title={editingId ? 'Edit Employee' : 'New Employee'} className="mb-6">
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Input label="Employee Code" name="employeeCode" value={form.employeeCode} onChange={handleChange} required />
            <Input label="First Name" name="firstName" value={form.firstName} onChange={handleChange} required />
            <Input label="Last Name" name="lastName" value={form.lastName} onChange={handleChange} required />
            <Input label="Email" name="email" type="email" value={form.email} onChange={handleChange} required />
            <Input label="Phone" name="phone" value={form.phone} onChange={handleChange} />
            <Input label="Designation" name="designation" value={form.designation} onChange={handleChange} />
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Department</label>
              <select
                name="departmentId"
                value={form.departmentId}
                onChange={handleChange}
                required
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
              >
                <option value="">Select department</option>
                {departments.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name}
                  </option>
                ))}
              </select>
            </div>
            <Input label="Date of Joining" name="dateOfJoining" type="date" value={form.dateOfJoining} onChange={handleChange} />
            <Input label="Basic Salary" name="basicSalary" type="number" value={form.basicSalary} onChange={handleChange} required />
            <Input label="HRA" name="hra" type="number" value={form.hra} onChange={handleChange} />
            <Input label="Allowances" name="allowances" type="number" value={form.allowances} onChange={handleChange} />
            <Input label="Address" name="address" value={form.address} onChange={handleChange} />
            <div className="md:col-span-3">
              <Button type="submit">{editingId ? 'Update Employee' : 'Save Employee'}</Button>
            </div>
          </form>
        </Card>
      )}

      <Card>
        <form onSubmit={handleSearch} className="flex gap-2 mb-4">
          <input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Search by name, email or code..."
            className="flex-1 border border-slate-300 rounded-lg px-3 py-2 text-sm"
          />
          <Button type="submit" variant="secondary">
            Search
          </Button>
        </form>
        <Table columns={columns} rows={employees} />
      </Card>
    </AppLayout>
  );
}

function Input({ label, ...props }) {
  return (
    <div>
      <label className="block text-sm font-medium text-slate-600 mb-1">{label}</label>
      <input {...props} className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm" />
    </div>
  );
}
