package com.payroll.mapper;

import com.payroll.dto.response.LeaveResponse;
import com.payroll.entity.LeaveRequest;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class LeaveMapper {

    public LeaveResponse toResponse(LeaveRequest leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1)
                .reason(leave.getReason())
                .status(leave.getStatus().name())
                .approvedBy(leave.getApprovedBy())
                .reviewedAt(leave.getReviewedAt())
                .build();
    }
}
