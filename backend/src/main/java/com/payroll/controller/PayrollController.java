package com.payroll.controller;

import com.payroll.dto.request.PayrollGenerateRequest;
import com.payroll.dto.response.ApiResponse;
import com.payroll.dto.response.PageResponse;
import com.payroll.dto.response.PayrollResponse;
import com.payroll.service.PayrollService;
import com.payroll.util.PageableUtil;
import com.payroll.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll Management")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<PayrollResponse>>> generatePayroll(
            @Valid @RequestBody PayrollGenerateRequest request) {
        List<PayrollResponse> response = payrollService.generatePayroll(request);
        return ResponseEntity.ok(ApiResponse.success("Payroll generated successfully", response));
    }

    @GetMapping("/history/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageResponse<PayrollResponse>>> getPayrollHistory(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageableUtil.build(page, size, "payYear", "DESC");
        PageResponse<PayrollResponse> response = payrollService.getPayrollHistory(employeeId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Payroll history retrieved successfully", response));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageResponse<PayrollResponse>>> getMyPayrollHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageableUtil.build(page, size, "payYear", "DESC");
        PageResponse<PayrollResponse> response =
                payrollService.getPayrollHistory(SecurityUtil.getCurrentEmployeeId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Payroll history retrieved successfully", response));
    }

    @GetMapping("/{payrollId}/payslip")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long payrollId) {
        byte[] pdf = payrollService.generatePayslipPdf(payrollId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payslip-" + payrollId + ".pdf")
                .body(pdf);
    }
}
