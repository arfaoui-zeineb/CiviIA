package com.civicpulse.communication.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Extracts the userId from JWT sub claim (or id claim).
     * If running in mock dev mode or unauthenticated, provides fallback.
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return 1L; // Fallback mock userId for dev/tests
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            if (sub != null) {
                try {
                    return Long.parseLong(sub);
                } catch (NumberFormatException e) {
                    // In case sub is a UUID string, fallback to numeric userId claim if present or hash
                    Long userIdClaim = jwt.getClaim("userId");
                    if (userIdClaim != null) {
                        return userIdClaim;
                    }
                    return Math.abs((long) sub.hashCode());
                }
            }
        }
        return 1L;
    }
}

