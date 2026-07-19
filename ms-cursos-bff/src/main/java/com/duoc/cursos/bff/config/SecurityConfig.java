package com.duoc.cursos.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
// Configuracion de seguridad para validar JWT y proteger los endpoints.
public class SecurityConfig {

    private static final String ROLE_CLAIM = "extension_consultaRole";

    @Bean
    // Permite el health sin autenticacion y protege el resto segun el rol recibido.
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/health", "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cursos", "/api/cursos/*")
                        .hasAnyAuthority("PROFESOR", "ROLE_PROFESOR", "ESTUDIANTE", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.GET, "/api/cursos/*/contenidos", "/api/contenidos/*", "/api/contenidos/*/descargar")
                        .hasAnyAuthority("PROFESOR", "ROLE_PROFESOR", "ESTUDIANTE", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.GET, "/api/cursos/*/examenes", "/api/examenes/*")
                        .hasAnyAuthority("PROFESOR", "ROLE_PROFESOR", "ESTUDIANTE", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/inscripciones")
                        .hasAnyAuthority("ESTUDIANTE", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/examenes/*/intentos")
                        .hasAnyAuthority("ESTUDIANTE", "ROLE_ESTUDIANTE")
                        .requestMatchers(HttpMethod.GET, "/api/estudiantes/*/intentos", "/api/estudiantes/*/calificaciones")
                        .hasAnyAuthority("PROFESOR", "ROLE_PROFESOR", "ESTUDIANTE", "ROLE_ESTUDIANTE")
                        .requestMatchers("/api/**").hasAnyAuthority("PROFESOR", "ROLE_PROFESOR")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    // Usa el claim de roles esperado por Azure AD para Spring Security.
    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    // Convierte los roles del token en authorities con y sin prefijo ROLE_.
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();
        addClaimValues(jwt.getClaim(ROLE_CLAIM), roles);

        return roles.stream()
                .filter(role -> role != null && !role.isBlank())
                .flatMap(role -> Stream.of(role, role.startsWith("ROLE_") ? role.substring(5) : "ROLE_" + role))
                .distinct()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
    }

    // Acepta el claim como lista o como texto separado por espacios o comas.
    private void addClaimValues(Object claimValue, Set<String> roles) {
        if (claimValue instanceof Collection<?> collection) {
            collection.stream()
                    .filter(value -> value != null)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .forEach(roles::add);
            return;
        }
        if (claimValue instanceof String text && !text.isBlank()) {
            for (String value : text.split("[,\\s]+")) {
                if (!value.isBlank()) {
                    roles.add(value.trim());
                }
            }
        }
    }
}
