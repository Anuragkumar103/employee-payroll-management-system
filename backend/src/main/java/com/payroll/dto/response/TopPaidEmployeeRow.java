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
public class TopPaidEmployeeRow {
    private Long employeeId;
    private String employeeName;
    private String departmentName;
    private BigDecimal netSalary;
}
