package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {
    private Long employeeId;
    private int year;
    private int totalEntitlement;
    private long leavesTaken;
    private long leavesRemaining;
}
