package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Integer payMonth;
    private Integer payYear;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal grossSalary;
    private BigDecimal taxDeduction;
    private BigDecimal otherDeductions;
    private BigDecimal netSalary;
    private Integer workingDays;
    private Integer paidDays;
    private String status;
    private LocalDateTime generatedAt;
}
