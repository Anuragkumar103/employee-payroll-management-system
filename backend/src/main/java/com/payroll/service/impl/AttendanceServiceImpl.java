package com.payroll.service.impl;

import com.payroll.dto.response.AttendanceResponse;
import com.payroll.dto.response.MonthlyAttendanceSummaryResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.entity.Attendance;
import com.payroll.entity.Employee;
import com.payroll.exception.BadRequestException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.mapper.AttendanceMapper;
import com.payroll.repository.AttendanceRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public AttendanceResponse checkIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        LocalDate today = LocalDate.now();
        attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today).ifPresent(a -> {
            throw new BadRequestException("Already checked in today");
        });

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(today)
                .checkInTime(LocalDateTime.now())
                .status(Attendance.AttendanceStatus.PRESENT)
                .build();

        return attendanceMapper.toResponse(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new BadRequestException("No check-in record found for today"));

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Already checked out today");
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        return attendanceMapper.toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public PageResponse<AttendanceResponse> getHistory(Long employeeId, Pageable pageable) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        Page<AttendanceResponse> page = attendanceRepository.findByEmployeeId(employeeId, pageable)
                .map(attendanceMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public MonthlyAttendanceSummaryResponse getMonthlySummary(Long employeeId, int month, int year) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        long present = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employeeId, start, end, Attendance.AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employeeId, start, end, Attendance.AttendanceStatus.ABSENT);
        long halfDay = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employeeId, start, end, Attendance.AttendanceStatus.HALF_DAY);
        long onLeave = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employeeId, start, end, Attendance.AttendanceStatus.ON_LEAVE);

        return MonthlyAttendanceSummaryResponse.builder()
                .employeeId(employeeId)
                .month(month)
                .year(year)
                .presentDays(present)
                .absentDays(absent)
                .halfDays(halfDay)
                .onLeaveDays(onLeave)
                .totalWorkingDays(yearMonth.lengthOfMonth())
                .build();
    }
}
