package com.payroll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "pay_month", "pay_year"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_month", nullable = false)
    private Integer payMonth;

    @Column(name = "pay_year", nullable = false)
    private Integer payYear;

    @Column(name = "basic_salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "hra", precision = 12, scale = 2)
    private BigDecimal hra;

    @Column(name = "allowances", precision = 12, scale = 2)
    private BigDecimal allowances;

    @Column(name = "gross_salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal grossSalary;

    @Column(name = "tax_deduction", precision = 12, scale = 2)
    private BigDecimal taxDeduction;

    @Column(name = "other_deductions", precision = 12, scale = 2)
    private BigDecimal otherDeductions;

    @Column(name = "net_salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal netSalary;

    @Column(name = "working_days")
    private Integer workingDays;

    @Column(name = "paid_days")
    private Integer paidDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PayrollStatus status = PayrollStatus.GENERATED;

    @Column(name = "payslip_path")
    private String payslipPath;

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;

    public enum PayrollStatus {
        GENERATED, PAID
    }
}
