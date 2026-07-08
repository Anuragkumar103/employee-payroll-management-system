package com.payroll.repository;

import com.payroll.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    Page<Attendance> findByEmployeeId(Long employeeId, Pageable pageable);

    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate start, LocalDate end);

    long countByEmployeeIdAndAttendanceDateBetweenAndStatus(
            Long employeeId, LocalDate start, LocalDate end, Attendance.AttendanceStatus status);
}
