package com.payroll.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.payroll.dto.request.PayrollGenerateRequest;
import com.payroll.dto.response.PageResponse;
import com.payroll.dto.response.PayrollResponse;
import com.payroll.entity.Attendance;
import com.payroll.entity.Employee;
import com.payroll.entity.Payroll;
import com.payroll.exception.BadRequestException;
import com.payroll.exception.DuplicateResourceException;
import com.payroll.exception.ResourceNotFoundException;
import com.payroll.mapper.PayrollMapper;
import com.payroll.repository.AttendanceRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollRepository;
import com.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    // Simplified progressive tax slabs (annualized gross), illustrative only.
    private static final BigDecimal SLAB_1_LIMIT = BigDecimal.valueOf(300000);
    private static final BigDecimal SLAB_2_LIMIT = BigDecimal.valueOf(700000);
    private static final BigDecimal SLAB_1_RATE = BigDecimal.valueOf(0.00);
    private static final BigDecimal SLAB_2_RATE = BigDecimal.valueOf(0.05);
    private static final BigDecimal SLAB_3_RATE = BigDecimal.valueOf(0.10);

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollMapper payrollMapper;

    @Override
    @Transactional
    public List<PayrollResponse> generatePayroll(PayrollGenerateRequest request) {
        List<Employee> employees;

        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with id: " + request.getEmployeeId()));
            employees = List.of(employee);
        } else {
            employees = employeeRepository.findAll().stream()
                    .filter(e -> e.getStatus() == Employee.EmployeeStatus.ACTIVE)
                    .toList();
        }

        if (employees.isEmpty()) {
            throw new BadRequestException("No active employees found to generate payroll for");
        }

        List<PayrollResponse> results = new java.util.ArrayList<>();

        for (Employee employee : employees) {
            if (payrollRepository.existsByEmployeeIdAndPayMonthAndPayYear(
                    employee.getId(), request.getMonth(), request.getYear())) {
                throw new DuplicateResourceException(
                        "Payroll already generated for employee " + employee.getEmployeeCode()
                                + " for " + request.getMonth() + "/" + request.getYear());
            }
            results.add(payrollMapper.toResponse(buildPayroll(employee, request.getMonth(), request.getYear())));
        }

        return results;
    }

    private Payroll buildPayroll(Employee employee, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalWorkingDays = yearMonth.lengthOfMonth();

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        long presentDays = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employee.getId(), start, end, Attendance.AttendanceStatus.PRESENT);
        long halfDays = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employee.getId(), start, end, Attendance.AttendanceStatus.HALF_DAY);
        long onLeaveDays = attendanceRepository.countByEmployeeIdAndAttendanceDateBetweenAndStatus(
                employee.getId(), start, end, Attendance.AttendanceStatus.ON_LEAVE);

        // Paid days = full present + half credit for half days + approved leave days (paid leave)
        BigDecimal paidDaysExact = BigDecimal.valueOf(presentDays)
                .add(BigDecimal.valueOf(halfDays).multiply(BigDecimal.valueOf(0.5)))
                .add(BigDecimal.valueOf(onLeaveDays));
        int paidDays = paidDaysExact.setScale(0, RoundingMode.HALF_UP).intValueExact();
        if (paidDays > totalWorkingDays) {
            paidDays = totalWorkingDays;
        }
        // If there's no attendance data at all for the month, default to full attendance
        // so payroll can still be generated (useful for demos / first-run environments).
        if (presentDays == 0 && halfDays == 0 && onLeaveDays == 0) {
            paidDays = totalWorkingDays;
        }

        BigDecimal basicSalary = nvl(employee.getBasicSalary());
        BigDecimal hra = nvl(employee.getHra());
        BigDecimal allowances = nvl(employee.getAllowances());

        BigDecimal attendanceRatio = BigDecimal.valueOf(paidDays)
                .divide(BigDecimal.valueOf(totalWorkingDays), 6, RoundingMode.HALF_UP);

        BigDecimal proratedBasic = basicSalary.multiply(attendanceRatio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal proratedHra = hra.multiply(attendanceRatio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal proratedAllowances = allowances.multiply(attendanceRatio).setScale(2, RoundingMode.HALF_UP);

        BigDecimal grossSalary = proratedBasic.add(proratedHra).add(proratedAllowances);

        BigDecimal tax = calculateMonthlyTax(grossSalary);
        BigDecimal otherDeductions = BigDecimal.ZERO;
        BigDecimal netSalary = grossSalary.subtract(tax).subtract(otherDeductions).setScale(2, RoundingMode.HALF_UP);

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .payMonth(month)
                .payYear(year)
                .basicSalary(proratedBasic)
                .hra(proratedHra)
                .allowances(proratedAllowances)
                .grossSalary(grossSalary)
                .taxDeduction(tax)
                .otherDeductions(otherDeductions)
                .netSalary(netSalary)
                .workingDays(totalWorkingDays)
                .paidDays(paidDays)
                .status(Payroll.PayrollStatus.GENERATED)
                .build();

        return payrollRepository.save(payroll);
    }

    /**
     * Simplified illustrative progressive tax calculation, applied monthly
     * against the (annualized) gross salary. Not a substitute for real
     * jurisdiction-specific payroll tax rules.
     */
    private BigDecimal calculateMonthlyTax(BigDecimal monthlyGross) {
        BigDecimal annualGross = monthlyGross.multiply(BigDecimal.valueOf(12));
        BigDecimal annualTax;

        if (annualGross.compareTo(SLAB_1_LIMIT) <= 0) {
            annualTax = BigDecimal.ZERO;
        } else if (annualGross.compareTo(SLAB_2_LIMIT) <= 0) {
            annualTax = annualGross.subtract(SLAB_1_LIMIT).multiply(SLAB_2_RATE);
        } else {
            BigDecimal slab2Tax = SLAB_2_LIMIT.subtract(SLAB_1_LIMIT).multiply(SLAB_2_RATE);
            BigDecimal slab3Tax = annualGross.subtract(SLAB_2_LIMIT).multiply(SLAB_3_RATE);
            annualTax = slab2Tax.add(slab3Tax);
        }

        return annualTax.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    @Override
    public PageResponse<PayrollResponse> getPayrollHistory(Long employeeId, Pageable pageable) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        Page<PayrollResponse> page = payrollRepository.findByEmployeeId(employeeId, pageable)
                .map(payrollMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    public byte[] generatePayslipPdf(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + payrollId));

        Employee employee = payroll.getEmployee();
        String monthName = java.time.Month.of(payroll.getPayMonth()).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Payslip - " + monthName + " " + payroll.getPayYear())
                    .setBold().setFontSize(16));
            document.add(new Paragraph("Employee: " + employee.getFirstName() + " " + employee.getLastName()
                    + " (" + employee.getEmployeeCode() + ")"));
            document.add(new Paragraph("Designation: " + safe(employee.getDesignation())));
            document.add(new Paragraph("Department: "
                    + (employee.getDepartment() != null ? employee.getDepartment().getName() : "-")));
            document.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
            addRow(table, "Working Days", String.valueOf(payroll.getWorkingDays()));
            addRow(table, "Paid Days", String.valueOf(payroll.getPaidDays()));
            addRow(table, "Basic Salary", format(payroll.getBasicSalary()));
            addRow(table, "HRA", format(payroll.getHra()));
            addRow(table, "Allowances", format(payroll.getAllowances()));
            addRow(table, "Gross Salary", format(payroll.getGrossSalary()));
            addRow(table, "Tax Deduction", format(payroll.getTaxDeduction()));
            addRow(table, "Other Deductions", format(payroll.getOtherDeductions()));
            addRow(table, "Net Salary", format(payroll.getNetSalary()));
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("This is a system-generated payslip and does not require a signature.")
                    .setItalic().setFontSize(9));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payslip PDF: " + e.getMessage(), e);
        }
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label)));
        table.addCell(new Cell().add(new Paragraph(value)));
    }

    private String format(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
    }

    private String safe(String value) {
        return value != null ? value : "-";
    }
}
