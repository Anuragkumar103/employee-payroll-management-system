package com.payroll.service;

import com.payroll.dto.request.LeaveDecisionRequest;
import com.payroll.dto.request.LeaveRequestDto;
import com.payroll.dto.response.LeaveBalanceResponse;
import com.payroll.dto.response.LeaveResponse;
import com.payroll.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface LeaveService {
    LeaveResponse applyLeave(Long employeeId, LeaveRequestDto request);
    LeaveResponse approveLeave(Long leaveId, String approverUsername, LeaveDecisionRequest request);
    LeaveResponse rejectLeave(Long leaveId, String approverUsername, LeaveDecisionRequest request);
    LeaveBalanceResponse getLeaveBalance(Long employeeId, int year);
    PageResponse<LeaveResponse> getLeavesForEmployee(Long employeeId, Pageable pageable);
    PageResponse<LeaveResponse> getPendingLeaves(Pageable pageable);
}
