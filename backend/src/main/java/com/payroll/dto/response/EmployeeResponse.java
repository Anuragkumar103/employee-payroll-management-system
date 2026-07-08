package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String designation;
    private Long departmentId;
    private String departmentName;
    private LocalDate dateOfJoining;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private String status;
    private String address;
}
