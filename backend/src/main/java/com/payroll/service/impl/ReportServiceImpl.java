package com.payroll.service.impl;

import com.payroll.dto.response.AttendanceReportRow;
import com.payroll.dto.response.DepartmentSalaryReportRow;
import com.payroll.dto.response.MonthlyPayrollReportRow;
import com.payroll.dto.response.TopPaidEmployeeRow;
import com.payroll.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<DepartmentSalaryReportRow> departmentSalaryReport() {
        String sql = """
                SELECT d.name AS department_name,
                       COUNT(e.id) AS employee_count,
                       COALESCE(SUM(e.basic_salary), 0) AS total_basic_salary,
                       COALESCE(SUM(p.net_salary), 0) AS total_net_salary,
                       COALESCE(AVG(p.net_salary), 0) AS average_net_salary
                FROM departments d
                LEFT JOIN employees e ON e.department_id = d.id AND e.status = 'ACTIVE'
                LEFT JOIN payrolls p ON p.employee_id = e.id
                GROUP BY d.id, d.name
                ORDER BY d.name
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> DepartmentSalaryReportRow.builder()
                .departmentName(rs.getString("department_name"))
                .employeeCount(rs.getLong("employee_count"))
                .totalBasicSalary(rs.getBigDecimal("total_basic_salary"))
                .totalNetSalary(rs.getBigDecimal("total_net_salary"))
                .averageNetSalary(rs.getBigDecimal("average_net_salary"))
                .build());
    }

    @Override
    public MonthlyPayrollReportRow monthlyPayrollReport(int month, int year) {
        String sql = """
                SELECT COUNT(*) AS employees_paid,
                       COALESCE(SUM(gross_salary), 0) AS total_gross,
                       COALESCE(SUM(tax_deduction + other_deductions), 0) AS total_deductions,
                       COALESCE(SUM(net_salary), 0) AS total_net
                FROM payrolls
                WHERE pay_month = ? AND pay_year = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> MonthlyPayrollReportRow.builder()
                .month(month)
                .year(year)
                .employeesPaid(rs.getLong("employees_paid"))
                .totalGrossSalary(rs.getBigDecimal("total_gross"))
                .totalDeductions(rs.getBigDecimal("total_deductions"))
                .totalNetSalary(rs.getBigDecimal("total_net"))
                .build(), month, year);
    }

    @Override
    public List<AttendanceReportRow> attendanceReport(int month, int year) {
        String sql = """
                SELECT e.id AS employee_id,
                       CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                       SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_days,
                       SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) AS absent_days,
                       SUM(CASE WHEN a.status = 'HALF_DAY' THEN 1 ELSE 0 END) AS half_days,
                       SUM(CASE WHEN a.status = 'ON_LEAVE' THEN 1 ELSE 0 END) AS on_leave_days
                FROM employees e
                LEFT JOIN attendance a ON a.employee_id = e.id
                    AND MONTH(a.attendance_date) = ? AND YEAR(a.attendance_date) = ?
                WHERE e.status = 'ACTIVE'
                GROUP BY e.id, employee_name
                ORDER BY employee_name
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> AttendanceReportRow.builder()
                .employeeId(rs.getLong("employee_id"))
                .employeeName(rs.getString("employee_name"))
                .presentDays(rs.getLong("present_days"))
                .absentDays(rs.getLong("absent_days"))
                .halfDays(rs.getLong("half_days"))
                .onLeaveDays(rs.getLong("on_leave_days"))
                .build(), month, year);
    }

    @Override
    public List<TopPaidEmployeeRow> topPaidEmployees(int limit) {
        String sql = """
                SELECT e.id AS employee_id,
                       CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
                       d.name AS department_name,
                       p.net_salary AS net_salary
                FROM payrolls p
                JOIN employees e ON e.id = p.employee_id
                LEFT JOIN departments d ON d.id = e.department_id
                WHERE p.pay_month = (SELECT MAX(pay_month) FROM payrolls WHERE pay_year = (SELECT MAX(pay_year) FROM payrolls))
                  AND p.pay_year = (SELECT MAX(pay_year) FROM payrolls)
                ORDER BY p.net_salary DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> TopPaidEmployeeRow.builder()
                .employeeId(rs.getLong("employee_id"))
                .employeeName(rs.getString("employee_name"))
                .departmentName(rs.getString("department_name"))
                .netSalary(rs.getBigDecimal("net_salary"))
                .build(), limit);
    }

    // Kept for readability of BigDecimal null-safety in Java 21 text blocks above.
    @SuppressWarnings("unused")
    private BigDecimal zeroIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
