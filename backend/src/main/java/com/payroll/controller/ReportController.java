package com.payroll.controller;

import com.payroll.dto.response.*;
import com.payroll.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/department-salary")
    public ResponseEntity<ApiResponse<List<DepartmentSalaryReportRow>>> departmentSalaryReport() {
        List<DepartmentSalaryReportRow> response = reportService.departmentSalaryReport();
        return ResponseEntity.ok(ApiResponse.success("Department salary report generated successfully", response));
    }

    @GetMapping("/monthly-payroll")
    public ResponseEntity<ApiResponse<MonthlyPayrollReportRow>> monthlyPayrollReport(
            @RequestParam int month, @RequestParam int year) {
        MonthlyPayrollReportRow response = reportService.monthlyPayrollReport(month, year);
        return ResponseEntity.ok(ApiResponse.success("Monthly payroll report generated successfully", response));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<AttendanceReportRow>>> attendanceReport(
            @RequestParam int month, @RequestParam int year) {
        List<AttendanceReportRow> response = reportService.attendanceReport(month, year);
        return ResponseEntity.ok(ApiResponse.success("Attendance report generated successfully", response));
    }

    @GetMapping("/top-paid-employees")
    public ResponseEntity<ApiResponse<List<TopPaidEmployeeRow>>> topPaidEmployees(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopPaidEmployeeRow> response = reportService.topPaidEmployees(limit);
        return ResponseEntity.ok(ApiResponse.success("Top paid employees report generated successfully", response));
    }
}
