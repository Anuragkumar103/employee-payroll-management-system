package com.payroll.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private String designation;

    @NotNull(message = "Department is required")
    private Long departmentId;

    private LocalDate dateOfJoining;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary must be positive")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal hra;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal allowances;

    private String address;
}
