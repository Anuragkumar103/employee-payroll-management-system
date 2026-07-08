package com.payroll.service.impl;

import com.payroll.dto.request.LeaveDecisionRequest;
import com.payroll.dto.request.LeaveRequestDto;
import com.payroll.dto.response.LeaveBalanceResponse;
import com.payroll.dto.response.LeaveResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.entity.Employee;
import com.payroll.entity.LeaveRequest;
import com.payroll.exception.BadRequestException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.mapper.LeaveMapper;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.LeaveRequestRepository;
import com.payroll.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private static final int ANNUAL_LEAVE_ENTITLEMENT = 24;

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveMapper leaveMapper;

    @Override
    @Transactional
    public LeaveResponse applyLeave(Long employeeId, LeaveRequestDto request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        LeaveRequest leave = LeaveRequest.builder()
                .employee(employee)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();

        return leaveMapper.toResponse(leaveRequestRepository.save(leave));
    }

    @Override
    @Transactional
    public LeaveResponse approveLeave(Long leaveId, String approverUsername, LeaveDecisionRequest request) {
        LeaveRequest leave = getPendingLeaveOrThrow(leaveId);
        leave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leave.setApprovedBy(approverUsername);
        leave.setReviewedAt(LocalDateTime.now());
        return leaveMapper.toResponse(leaveRequestRepository.save(leave));
    }

    @Override
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId, String approverUsername, LeaveDecisionRequest request) {
        LeaveRequest leave = getPendingLeaveOrThrow(leaveId);
        leave.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leave.setApprovedBy(approverUsername);
        leave.setReviewedAt(LocalDateTime.now());
        return leaveMapper.toResponse(leaveRequestRepository.save(leave));
    }

    private LeaveRequest getPendingLeaveOrThrow(Long leaveId) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BadRequestException("Leave request has already been reviewed");
        }
        return leave;
    }

    @Override
    public LeaveBalanceResponse getLeaveBalance(Long employeeId, int year) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        List<LeaveRequest> approvedLeaves = leaveRequestRepository
                .findByEmployeeIdAndStatusAndStartDateBetween(
                        employeeId, LeaveRequest.LeaveStatus.APPROVED, yearStart, yearEnd);

        long daysTaken = approvedLeaves.stream()
                .mapToLong(l -> ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate()) + 1)
                .sum();

        return LeaveBalanceResponse.builder()
                .employeeId(employeeId)
                .year(year)
                .totalEntitlement(ANNUAL_LEAVE_ENTITLEMENT)
                .leavesTaken(daysTaken)
                .leavesRemaining(Math.max(0, ANNUAL_LEAVE_ENTITLEMENT - daysTaken))
                .build();
    }

    @Override
    public PageResponse<LeaveResponse> getLeavesForEmployee(Long employeeId, Pageable pageable) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        Page<LeaveResponse> page = leaveRequestRepository.findByEmployeeId(employeeId, pageable)
                .map(leaveMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public PageResponse<LeaveResponse> getPendingLeaves(Pageable pageable) {
        Page<LeaveResponse> page = leaveRequestRepository
                .findByStatus(LeaveRequest.LeaveStatus.PENDING, pageable)
                .map(leaveMapper::toResponse);
        return PageResponse.from(page);
    }
}
