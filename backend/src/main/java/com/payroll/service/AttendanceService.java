package com.payroll.service;

import com.payroll.dto.response.AttendanceResponse;
import com.payroll.dto.response.MonthlyAttendanceSummaryResponse;
import com.payroll.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AttendanceService {
    AttendanceResponse checkIn(Long employeeId);
    AttendanceResponse checkOut(Long employeeId);
    PageResponse<AttendanceResponse> getHistory(Long employeeId, Pageable pageable);
    MonthlyAttendanceSummaryResponse getMonthlySummary(Long employeeId, int month, int year);
}
