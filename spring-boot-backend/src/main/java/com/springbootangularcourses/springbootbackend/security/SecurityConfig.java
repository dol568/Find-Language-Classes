package com.springbootangularcourses.springbootbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher.Builder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JWTAccessDeniedHandler jwtAccessDeniedHandler;
    private final JWTAuthorizationFilter jwtAuthorizationFilter;
    private final JWTForbiddenEntryPoint jwtForbiddenEntryPoint;
    private final PasswordEncoder passwordEncoder;
    private final SpaWebFilter spaWebFilter;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return authenticationProvider;
    }

    @Bean()
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/h2-console/**", "/favicon.ico", "*.png", "*.gif", "*.svg",
                                "*.jpg", "*.html", "*.css", "*.js", "/static/**", "/api/login",
                                "/api/register", "/", "/assets/**").permitAll()
                        .requestMatchers("/ws/**", "/ws/**/**", "/ws/**/**/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(spaWebFilter, BasicAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtForbiddenEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(jwtAccessDeniedHandler))
                .authenticationProvider(authenticationProvider())
                .authenticationManager(authenticationManager());
        return http.build();
    }
}
