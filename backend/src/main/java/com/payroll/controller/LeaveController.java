package com.payroll.controller;

import com.payroll.dto.request.LeaveDecisionRequest;
import com.payroll.dto.request.LeaveRequestDto;
import com.payroll.dto.response.ApiResponse;
import com.payroll.dto.response.LeaveBalanceResponse;
import com.payroll.dto.response.LeaveResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.service.LeaveService;
import com.payroll.util.PageableUtil;
import com.payroll.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Management")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveResponse>> applyLeave(@Valid @RequestBody LeaveRequestDto request) {
        LeaveResponse response = leaveService.applyLeave(SecurityUtil.getCurrentEmployeeId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave request submitted successfully", response));
    }

    @PutMapping("/{leaveId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<LeaveResponse>> approveLeave(
            @PathVariable Long leaveId, @RequestBody(required = false) LeaveDecisionRequest request) {
        LeaveResponse response = leaveService.approveLeave(
                leaveId, SecurityUtil.getCurrentUsername(), request != null ? request : new LeaveDecisionRequest());
        return ResponseEntity.ok(ApiResponse.success("Leave request approved", response));
    }

    @PutMapping("/{leaveId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<LeaveResponse>> rejectLeave(
            @PathVariable Long leaveId, @RequestBody(required = false) LeaveDecisionRequest request) {
        LeaveResponse response = leaveService.rejectLeave(
                leaveId, SecurityUtil.getCurrentUsername(), request != null ? request : new LeaveDecisionRequest());
        return ResponseEntity.ok(ApiResponse.success("Leave request rejected", response));
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> getLeaveBalance(
            @RequestParam(required = false) Integer year) {
        int targetYear = year != null ? year : Year.now().getValue();
        LeaveBalanceResponse response = leaveService.getLeaveBalance(SecurityUtil.getCurrentEmployeeId(), targetYear);
        return ResponseEntity.ok(ApiResponse.success("Leave balance retrieved successfully", response));
    }

    @GetMapping("/my-leaves")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageResponse<LeaveResponse>>> getMyLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageableUtil.build(page, size, "createdAt", "DESC");
        PageResponse<LeaveResponse> response =
                leaveService.getLeavesForEmployee(SecurityUtil.getCurrentEmployeeId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Leave requests retrieved successfully", response));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<PageResponse<LeaveResponse>>> getPendingLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageableUtil.build(page, size, "createdAt", "ASC");
        PageResponse<LeaveResponse> response = leaveService.getPendingLeaves(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending leave requests retrieved successfully", response));
    }
}
