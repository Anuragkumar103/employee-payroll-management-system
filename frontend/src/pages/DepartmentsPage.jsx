import React, { useEffect, useState } from 'react';
import AppLayout from '../layouts/AppLayout';
import { Alert, Button, Card, Table } from '../components/UI';
import { departmentService } from '../services/departmentService';
import { useAuth } from '../context/AuthContext';

export default function DepartmentsPage() {
  const { user } = useAuth();
  const canManage = user?.role === 'ADMIN' || user?.role === 'HR';
  const [departments, setDepartments] = useState([]);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    try {
      const { data } = await departmentService.getAll();
      setDepartments(data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load departments');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const resetForm = () => {
    setName('');
    setDescription('');
    setEditingId(null);
    setShowForm(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      if (editingId) {
        await departmentService.update(editingId, { name, description });
        setSuccess('Department updated successfully');
      } else {
        await departmentService.create({ name, description });
        setSuccess('Department created successfully');
      }
      resetForm();
      load();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save department');
    }
  };

  const handleEdit = (dept) => {
    setName(dept.name);
    setDescription(dept.description || '');
    setEditingId(dept.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this department?')) return;
    try {
      await departmentService.remove(id);
      load();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete department (it may still have employees)');
    }
  };

  const columns = [
    { key: 'name', header: 'Name' },
    { key: 'description', header: 'Description' },
    { key: 'employeeCount', header: 'Employees' },
    ...(canManage
      ? [
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
        ]
      : [])
  ];

  return (
    <AppLayout>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Departments</h1>
        {canManage && (
          <Button onClick={() => (showForm ? resetForm() : setShowForm(true))}>
            {showForm ? 'Cancel' : '+ Add Department'}
          </Button>
        )}
      </div>

      <Alert type="error">{error}</Alert>
      <Alert type="success">{success}</Alert>

      {showForm && (
        <Card title={editingId ? 'Edit Department' : 'New Department'} className="mb-6">
          <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Name</label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-600 mb-1">Description</label>
              <input
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm"
              />
            </div>
            <div className="md:col-span-2">
              <Button type="submit">{editingId ? 'Update' : 'Save'}</Button>
            </div>
          </form>
        </Card>
      )}

      <Card>
        <Table columns={columns} rows={departments} />
      </Card>
    </AppLayout>
  );
}
