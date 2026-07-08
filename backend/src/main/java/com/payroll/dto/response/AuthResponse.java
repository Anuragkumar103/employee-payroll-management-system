package com.payroll.dto.response;

import com.payroll.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private String email;
    private RoleName role;
    private Long employeeId;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
