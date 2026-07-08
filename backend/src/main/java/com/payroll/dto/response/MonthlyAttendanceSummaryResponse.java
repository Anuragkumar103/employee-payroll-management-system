package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAttendanceSummaryResponse {
    private Long employeeId;
    private int month;
    private int year;
    private long presentDays;
    private long absentDays;
    private long halfDays;
    private long onLeaveDays;
    private long totalWorkingDays;
}
