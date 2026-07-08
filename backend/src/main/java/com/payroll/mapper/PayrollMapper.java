package com.payroll.mapper;

import com.payroll.dto.response.PayrollResponse;
import com.payroll.entity.Payroll;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {

    public PayrollResponse toResponse(Payroll payroll) {
        return PayrollResponse.builder()
                .id(payroll.getId())
                .employeeId(payroll.getEmployee().getId())
                .employeeName(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName())
                .payMonth(payroll.getPayMonth())
                .payYear(payroll.getPayYear())
                .basicSalary(payroll.getBasicSalary())
                .hra(payroll.getHra())
                .allowances(payroll.getAllowances())
                .grossSalary(payroll.getGrossSalary())
                .taxDeduction(payroll.getTaxDeduction())
                .otherDeductions(payroll.getOtherDeductions())
                .netSalary(payroll.getNetSalary())
                .workingDays(payroll.getWorkingDays())
                .paidDays(payroll.getPaidDays())
                .status(payroll.getStatus().name())
                .generatedAt(payroll.getGeneratedAt())
                .build();
    }
}
