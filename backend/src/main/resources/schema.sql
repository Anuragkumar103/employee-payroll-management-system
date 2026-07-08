-- ============================================================
-- Employee Payroll Management System — MySQL 8 Schema
-- Database: payroll_db
-- Tables use IF NOT EXISTS so this is safe to run alongside
-- Hibernate's ddl-auto=update on first boot.
-- ============================================================

CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

-- ---------------------------------------------------------
-- departments
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME
);

-- ---------------------------------------------------------
-- employees
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    designation VARCHAR(100),
    department_id BIGINT,
    date_of_joining DATE,
    basic_salary DECIMAL(12,2),
    hra DECIMAL(12,2),
    allowances DECIMAL(12,2),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    address VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_employees_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- ---------------------------------------------------------
-- users (auth)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME,
    employee_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- ---------------------------------------------------------
-- attendance
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time DATETIME,
    check_out_time DATETIME,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT uq_attendance_employee_date UNIQUE (employee_id, attendance_date)
);

-- ---------------------------------------------------------
-- leave_requests
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(50),
    reviewed_at DATETIME,
    created_at DATETIME,
    CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- ---------------------------------------------------------
-- payrolls
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS payrolls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    pay_month INT NOT NULL,
    pay_year INT NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL,
    hra DECIMAL(12,2),
    allowances DECIMAL(12,2),
    gross_salary DECIMAL(12,2) NOT NULL,
    tax_deduction DECIMAL(12,2),
    other_deductions DECIMAL(12,2),
    net_salary DECIMAL(12,2) NOT NULL,
    working_days INT,
    paid_days INT,
    status VARCHAR(20) NOT NULL DEFAULT 'GENERATED',
    payslip_path VARCHAR(255),
    generated_at DATETIME,
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT uq_payroll_employee_month_year UNIQUE (employee_id, pay_month, pay_year)
);

-- ---------------------------------------------------------
-- Helpful indexes for reporting queries
-- ---------------------------------------------------------
CREATE INDEX idx_employees_department ON employees(department_id);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_payrolls_month_year ON payrolls(pay_month, pay_year);
CREATE INDEX idx_leave_status ON leave_requests(status);
