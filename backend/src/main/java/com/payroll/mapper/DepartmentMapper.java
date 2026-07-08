package com.payroll.mapper;

import com.payroll.dto.response.DepartmentResponse;
import com.payroll.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .employeeCount(department.getEmployees() != null ? department.getEmployees().size() : 0)
                .build();
    }
}
