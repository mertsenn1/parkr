package com.parkr.parkr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf()
        .disable()
        .cors()
        .and()
        .authorizeHttpRequests()
        .requestMatchers("/users/sign-up", "/users/sign-in", "/users/validate-token", "/swagger-ui/index.html", "/", "/swagger-ui/", "/swagger-ui/**", "/v3/api-docs/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        /* 
        .exceptionHandling()
        .authenticationEntryPoint((request, response, authException) -> 
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not authenticated"))
        .accessDeniedHandler((request, response, accessDeniedException) -> 
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is not authorized for the following request"))
        .and()
        */
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout()
        .logoutUrl("/users/sign-out") 
        .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler((request, response, authentication) ->
            SecurityContextHolder.clearContext()
        );
        return http.build();
    }
}