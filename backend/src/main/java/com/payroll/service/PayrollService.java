package com.payroll.service;

import com.payroll.dto.request.PayrollGenerateRequest;
import com.payroll.dto.response.PageResponse;
import com.payroll.dto.response.PayrollResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PayrollService {
    List<PayrollResponse> generatePayroll(PayrollGenerateRequest request);
    PageResponse<PayrollResponse> getPayrollHistory(Long employeeId, Pageable pageable);
    byte[] generatePayslipPdf(Long payrollId);
}
