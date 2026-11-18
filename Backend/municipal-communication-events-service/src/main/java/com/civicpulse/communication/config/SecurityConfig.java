package com.civicpulse.communication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${dev.security.mock-user-enabled:false}")
    private boolean devMockUserEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (devMockUserEnabled) {
            // Dev profile / testing without Keycloak: allow all requests
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http.authorizeHttpRequests(auth -> auth
                    // Public Swagger & Actuator
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**").permitAll()

                    // CITIZEN can read published announcements and open events
                    .requestMatchers(HttpMethod.GET, "/api/v1/announcements/**", "/api/v1/events/**", "/api/v1/categories/**", "/api/v1/venues/**").permitAll()

                    // CITIZEN registration endpoints
                    .requestMatchers(HttpMethod.POST, "/api/v1/events/*/registrations", "/api/v1/events/*/registrations/cancel").hasAnyRole("CITIZEN", "DEPARTMENT_MANAGER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/events/*/registrations").hasAnyRole("CITIZEN", "DEPARTMENT_MANAGER", "ADMIN")

                    // DEPARTMENT_MANAGER: publish announcements and events for their department
                    .requestMatchers("/api/v1/announcements/**", "/api/v1/events/**").hasAnyRole("DEPARTMENT_MANAGER", "ADMIN")

                    // ADMIN: manage categories and venues, full access
                    .requestMatchers("/api/v1/categories/**", "/api/v1/venues/**").hasRole("ADMIN")

                    .anyRequest().authenticated()
            ).oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        }

        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Extract realm_access.roles from Keycloak token
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                authorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList()));
            }

            return authorities;
        });
        return jwtConverter;
    }
}

