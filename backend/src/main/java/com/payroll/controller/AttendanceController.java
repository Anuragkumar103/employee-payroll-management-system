package com.payroll.controller;

import com.payroll.dto.response.ApiResponse;
import com.payroll.dto.response.AttendanceResponse;
import com.payroll.dto.response.MonthlyAttendanceSummaryResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.service.AttendanceService;
import com.payroll.util.PageableUtil;
import com.payroll.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn() {
        AttendanceResponse response = attendanceService.checkIn(SecurityUtil.getCurrentEmployeeId());
        return ResponseEntity.ok(ApiResponse.success("Checked in successfully", response));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut() {
        AttendanceResponse response = attendanceService.checkOut(SecurityUtil.getCurrentEmployeeId());
        return ResponseEntity.ok(ApiResponse.success("Checked out successfully", response));
    }

    @GetMapping("/history/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageResponse<AttendanceResponse>>> getHistory(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageableUtil.build(page, size, "attendanceDate", "DESC");
        PageResponse<AttendanceResponse> response = attendanceService.getHistory(employeeId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Attendance history retrieved successfully", response));
    }

    @GetMapping("/summary/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<MonthlyAttendanceSummaryResponse>> getMonthlySummary(
            @PathVariable Long employeeId,
            @RequestParam int month,
            @RequestParam int year) {

        MonthlyAttendanceSummaryResponse response = attendanceService.getMonthlySummary(employeeId, month, year);
        return ResponseEntity.ok(ApiResponse.success("Monthly attendance summary retrieved successfully", response));
    }
}
