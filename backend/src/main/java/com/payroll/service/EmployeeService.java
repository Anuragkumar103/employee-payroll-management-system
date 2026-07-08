package com.payroll.service;

import com.payroll.dto.request.EmployeeRequest;
import com.payroll.dto.response.EmployeeResponse;
import com.payroll.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponse addEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
    EmployeeResponse getEmployeeById(Long id);
    PageResponse<EmployeeResponse> getAllEmployees(Pageable pageable);
    PageResponse<EmployeeResponse> searchEmployees(String keyword, Pageable pageable);
}
