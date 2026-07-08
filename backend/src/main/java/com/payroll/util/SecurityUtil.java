package com.payroll.util;

import com.payroll.entity.User;
import com.payroll.exception.BadRequestException;
import com.payroll.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new BadRequestException("No authenticated user found in security context");
        }
        return userDetails.getUser();
    }

    public static Long getCurrentEmployeeId() {
        User user = getCurrentUser();
        if (user.getEmployee() == null) {
            throw new BadRequestException("Current user is not linked to an employee profile");
        }
        return user.getEmployee().getId();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}
