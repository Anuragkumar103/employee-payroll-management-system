package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSalaryReportRow {
    private String departmentName;
    private long employeeCount;
    private BigDecimal totalBasicSalary;
    private BigDecimal totalNetSalary;
    private BigDecimal averageNetSalary;
}
