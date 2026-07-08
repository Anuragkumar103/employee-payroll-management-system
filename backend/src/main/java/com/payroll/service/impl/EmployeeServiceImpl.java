package com.payroll.service.impl;

import com.payroll.dto.request.EmployeeRequest;
import com.payroll.dto.response.EmployeeResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.entity.Department;
import com.payroll.entity.Employee;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.mapper.EmployeeMapper;
import com.payroll.repository.DepartmentRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee already exists with email: " + request.getEmail());
        }
        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DuplicateResourceException("Employee code already in use: " + request.getEmployeeCode());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartmentId()));

        Employee employee = Employee.builder()
                .employeeCode(request.getEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .department(department)
                .dateOfJoining(request.getDateOfJoining())
                .basicSalary(request.getBasicSalary())
                .hra(request.getHra())
                .allowances(request.getAllowances())
                .address(request.getAddress())
                .status(Employee.EmployeeStatus.ACTIVE)
                .build();

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employeeRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Email already in use: " + request.getEmail());
            }
        });

        employeeRepository.findByEmployeeCode(request.getEmployeeCode()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Employee code already in use: " + request.getEmployeeCode());
            }
        });

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartmentId()));

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDesignation(request.getDesignation());
        employee.setDepartment(department);
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setBasicSalary(request.getBasicSalary());
        employee.setHra(request.getHra());
        employee.setAllowances(request.getAllowances());
        employee.setAddress(request.getAddress());

        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setStatus(Employee.EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toResponse(employee);
    }

    @Override
    public PageResponse<EmployeeResponse> getAllEmployees(Pageable pageable) {
        Page<EmployeeResponse> page = employeeRepository.findAll(pageable).map(employeeMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public PageResponse<EmployeeResponse> searchEmployees(String keyword, Pageable pageable) {
        Page<EmployeeResponse> page = employeeRepository.search(keyword, pageable).map(employeeMapper::toResponse);
        return PageResponse.from(page);
    }
}
