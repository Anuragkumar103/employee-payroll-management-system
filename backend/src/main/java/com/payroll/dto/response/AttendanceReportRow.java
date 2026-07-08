package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportRow {
    private Long employeeId;
    private String employeeName;
    private long presentDays;
    private long absentDays;
    private long halfDays;
    private long onLeaveDays;
}
