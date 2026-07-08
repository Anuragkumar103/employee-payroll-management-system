package com.payroll.mapper;

import com.payroll.dto.response.EmployeeResponse;
import com.payroll.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .designation(employee.getDesignation())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .dateOfJoining(employee.getDateOfJoining())
                .basicSalary(employee.getBasicSalary())
                .hra(employee.getHra())
                .allowances(employee.getAllowances())
                .status(employee.getStatus().name())
                .address(employee.getAddress())
                .build();
    }
}
