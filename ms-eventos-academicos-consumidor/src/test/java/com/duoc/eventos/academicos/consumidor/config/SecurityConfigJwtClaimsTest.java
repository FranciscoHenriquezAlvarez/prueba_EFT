package com.duoc.eventos.academicos.consumidor.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebas que validan la conversion del claim de rol usado por Spring Security en el consumidor.
class SecurityConfigJwtClaimsTest {

    @Test
    void debeExtraerRolDesdeClaimOficialComoString() throws Exception {
        Set<String> authorities = extractAuthorities(claimJwt("extension_consultaRole", "PROFESOR"));

        assertTrue(authorities.contains(SecurityRoles.ROLE_PROFESOR));
        assertTrue(authorities.contains("ROLE_" + SecurityRoles.ROLE_PROFESOR));
    }

    @Test
    void debeExtraerRolDesdeClaimOficialComoLista() throws Exception {
        Set<String> authorities = extractAuthorities(claimJwt("extension_consultaRole", List.of("ESTUDIANTE")));

        assertTrue(authorities.contains(SecurityRoles.ROLE_ESTUDIANTE));
        assertTrue(authorities.contains("ROLE_" + SecurityRoles.ROLE_ESTUDIANTE));
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractAuthorities(Jwt jwt) throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        Method method = SecurityConfig.class.getDeclaredMethod("extractAuthorities", Jwt.class);
        method.setAccessible(true);

        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) method.invoke(securityConfig, jwt);
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private Jwt claimJwt(String claimName, Object claimValue) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(claimName, claimValue)
                .build();
    }
}
