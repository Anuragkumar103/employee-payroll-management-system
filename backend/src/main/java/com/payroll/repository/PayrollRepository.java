package com.payroll.repository;

import com.payroll.entity.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Page<Payroll> findByEmployeeId(Long employeeId, Pageable pageable);

    Optional<Payroll> findByEmployeeIdAndPayMonthAndPayYear(Long employeeId, Integer payMonth, Integer payYear);

    boolean existsByEmployeeIdAndPayMonthAndPayYear(Long employeeId, Integer payMonth, Integer payYear);

    Page<Payroll> findByPayMonthAndPayYear(Integer payMonth, Integer payYear, Pageable pageable);
}
