package com.payroll.service;

import com.payroll.dto.request.DepartmentRequest;
import com.payroll.dto.response.DepartmentResponse;
import com.payroll.dto.response.EmployeeResponse;
import com.payroll.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse addDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
    void deleteDepartment(Long id);
    DepartmentResponse getDepartmentById(Long id);
    List<DepartmentResponse> getAllDepartments();
    PageResponse<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable);
}
