package com.duoc.cursos.bff.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        String prefixed = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (role.equals(authority.getAuthority()) || prefixed.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
