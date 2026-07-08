-- ============================================================
-- Seed data for Employee Payroll Management System
-- Passwords below are BCrypt hashes:
--   admin   -> Admin@123
--   hr1     -> Hr@123456
--   jdoe    -> Employee@123
-- ============================================================

-- ---------------------------------------------------------
-- Departments
-- ---------------------------------------------------------
INSERT INTO departments (id, name, description, created_at)
SELECT 1, 'Engineering', 'Product engineering and platform teams', NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 1);

INSERT INTO departments (id, name, description, created_at)
SELECT 2, 'Human Resources', 'HR, recruitment and employee relations', NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 2);

INSERT INTO departments (id, name, description, created_at)
SELECT 3, 'Finance', 'Accounting, payroll and financial planning', NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE id = 3);

-- ---------------------------------------------------------
-- Employees
-- ---------------------------------------------------------
INSERT INTO employees (id, employee_code, first_name, last_name, email, phone, designation,
                        department_id, date_of_joining, basic_salary, hra, allowances, status, address,
                        created_at, updated_at)
SELECT 1, 'EMP-1001', 'Anurag', 'Kumar', 'anurag.kumar@payroll.local', '9999900001', 'Engineering Manager',
       1, '2022-01-10', 90000.00, 20000.00, 10000.00, 'ACTIVE', 'Bengaluru, India', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE id = 1);

INSERT INTO employees (id, employee_code, first_name, last_name, email, phone, designation,
                        department_id, date_of_joining, basic_salary, hra, allowances, status, address,
                        created_at, updated_at)
SELECT 2, 'EMP-1002', 'Priya', 'Sharma', 'priya.sharma@payroll.local', '9999900002', 'HR Executive',
       2, '2023-03-15', 55000.00, 12000.00, 5000.00, 'ACTIVE', 'Pune, India', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE id = 2);

INSERT INTO employees (id, employee_code, first_name, last_name, email, phone, designation,
                        department_id, date_of_joining, basic_salary, hra, allowances, status, address,
                        created_at, updated_at)
SELECT 3, 'EMP-1003', 'John', 'Doe', 'john.doe@payroll.local', '9999900003', 'Software Engineer',
       1, '2023-07-01', 65000.00, 15000.00, 6000.00, 'ACTIVE', 'Hyderabad, India', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE id = 3);

-- ---------------------------------------------------------
-- Users (login credentials)
-- ---------------------------------------------------------
INSERT INTO users (id, username, email, password, role, enabled, employee_id, created_at, updated_at)
SELECT 1, 'admin', 'admin@payroll.local',
       '$2b$10$8VvRSf9HbCrKv/Q2KNovO..ZLKGy6rz41TSahBBGM6C8pnfotsJiO',
       'ADMIN', TRUE, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 1);

INSERT INTO users (id, username, email, password, role, enabled, employee_id, created_at, updated_at)
SELECT 2, 'hr1', 'priya.sharma@payroll.local',
       '$2b$10$R61dX7CywG.ALhjop4aCxubsZ7r/i8aJY7cpMa0svAWeK83ess9sO',
       'HR', TRUE, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 2);

INSERT INTO users (id, username, email, password, role, enabled, employee_id, created_at, updated_at)
SELECT 3, 'jdoe', 'john.doe@payroll.local',
       '$2b$10$Qnf3J0..Ex8J8nH6g8u0Y.ARJ1JZJX02kchllxo4hmRBe.Iro.KHW',
       'EMPLOYEE', TRUE, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 3);

-- ---------------------------------------------------------
-- Sample attendance (current month, first few days)
-- ---------------------------------------------------------
INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status)
SELECT 3, CURDATE() - INTERVAL 1 DAY, (CURDATE() - INTERVAL 1 DAY) + INTERVAL 9 HOUR, (CURDATE() - INTERVAL 1 DAY) + INTERVAL 18 HOUR, 'PRESENT'
WHERE NOT EXISTS (
    SELECT 1 FROM attendance WHERE employee_id = 3 AND attendance_date = CURDATE() - INTERVAL 1 DAY
);

INSERT INTO attendance (employee_id, attendance_date, check_in_time, check_out_time, status)
SELECT 3, CURDATE() - INTERVAL 2 DAY, (CURDATE() - INTERVAL 2 DAY) + INTERVAL 9 HOUR, (CURDATE() - INTERVAL 2 DAY) + INTERVAL 17 HOUR, 'PRESENT'
WHERE NOT EXISTS (
    SELECT 1 FROM attendance WHERE employee_id = 3 AND attendance_date = CURDATE() - INTERVAL 2 DAY
);

-- ---------------------------------------------------------
-- Sample leave request
-- ---------------------------------------------------------
INSERT INTO leave_requests (employee_id, start_date, end_date, reason, status, created_at)
SELECT 3, CURDATE() + INTERVAL 5 DAY, CURDATE() + INTERVAL 6 DAY, 'Family function', 'PENDING', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM leave_requests WHERE employee_id = 3 AND reason = 'Family function'
);
