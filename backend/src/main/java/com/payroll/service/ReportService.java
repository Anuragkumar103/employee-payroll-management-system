package com.payroll.service;

import com.payroll.dto.response.AttendanceReportRow;
import com.payroll.dto.response.DepartmentSalaryReportRow;
import com.payroll.dto.response.MonthlyPayrollReportRow;
import com.payroll.dto.response.TopPaidEmployeeRow;

import java.util.List;

public interface ReportService {
    List<DepartmentSalaryReportRow> departmentSalaryReport();
    MonthlyPayrollReportRow monthlyPayrollReport(int month, int year);
    List<AttendanceReportRow> attendanceReport(int month, int year);
    List<TopPaidEmployeeRow> topPaidEmployees(int limit);
}
