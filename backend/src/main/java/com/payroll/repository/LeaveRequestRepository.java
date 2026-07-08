package com.payroll.repository;

import com.payroll.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<LeaveRequest> findByStatus(LeaveRequest.LeaveStatus status, Pageable pageable);

    List<LeaveRequest> findByEmployeeIdAndStatusAndStartDateBetween(
            Long employeeId, LeaveRequest.LeaveStatus status, LocalDate start, LocalDate end);

    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveRequest.LeaveStatus status);
}
