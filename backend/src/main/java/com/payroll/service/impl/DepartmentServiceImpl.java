package com.payroll.service.impl;

import com.payroll.dto.request.DepartmentRequest;
import com.payroll.dto.response.DepartmentResponse;
import com.payroll.dto.response.EmployeeResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.entity.Department;
import com.payroll.exception.BadRequestException;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.mapper.DepartmentMapper;
import com.payroll.mapper.EmployeeMapper;
import com.payroll.repository.DepartmentRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public DepartmentResponse addDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Department already exists with name: " + request.getName());
        }
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        departmentRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Department already exists with name: " + request.getName());
            }
        });

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        if (!department.getEmployees().isEmpty()) {
            throw new BadRequestException(
                    "Cannot delete department that still has employees assigned. Reassign employees first.");
        }
        departmentRepository.delete(department);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return departmentMapper.toResponse(department);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @Override
    public PageResponse<EmployeeResponse> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        }
        Page<EmployeeResponse> page = employeeRepository.findByDepartmentId(departmentId, pageable)
                .map(employeeMapper::toResponse);
        return PageResponse.from(page);
    }
}
