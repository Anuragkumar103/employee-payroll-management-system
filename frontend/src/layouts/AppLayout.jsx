import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', roles: ['ADMIN', 'HR', 'EMPLOYEE'] },
  { to: '/employees', label: 'Employees', roles: ['ADMIN', 'HR'] },
  { to: '/departments', label: 'Departments', roles: ['ADMIN', 'HR', 'EMPLOYEE'] },
  { to: '/attendance', label: 'Attendance', roles: ['ADMIN', 'HR', 'EMPLOYEE'] },
  { to: '/leaves', label: 'Leaves', roles: ['ADMIN', 'HR', 'EMPLOYEE'] },
  { to: '/payroll', label: 'Payroll', roles: ['ADMIN', 'HR', 'EMPLOYEE'] },
  { to: '/reports', label: 'Reports', roles: ['ADMIN', 'HR'] }
];

export default function AppLayout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="flex h-screen bg-slate-50">
      <aside className="w-64 bg-slate-900 text-slate-200 flex flex-col">
        <div className="px-6 py-5 border-b border-slate-800">
          <h1 className="text-lg font-bold text-white">Payroll System</h1>
          <p className="text-xs text-slate-400 mt-1">Enterprise Edition</p>
        </div>
        <nav className="flex-1 px-3 py-4 space-y-1">
          {NAV_ITEMS.filter((item) => item.roles.includes(user?.role)).map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `block px-4 py-2 rounded-lg text-sm font-medium transition ${
                  isActive ? 'bg-brand-600 text-white' : 'text-slate-300 hover:bg-slate-800'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="px-4 py-4 border-t border-slate-800">
          <p className="text-sm text-slate-300">{user?.username}</p>
          <p className="text-xs text-slate-500 mb-3">{user?.role}</p>
          <button
            onClick={handleLogout}
            className="w-full text-left text-sm text-red-400 hover:text-red-300"
          >
            Logout
          </button>
        </div>
      </aside>
      <main className="flex-1 overflow-y-auto p-8">{children}</main>
    </div>
  );
}
