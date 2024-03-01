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
    private final Builder mvc;
    private final PasswordEncoder passwordEncoder;

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
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/favicon.ico")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.png")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.gif")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.svg")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.jpg")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.html")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.css")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/*/*.js")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/login")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/register")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/ws/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("ws")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("ws/**")).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtForbiddenEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(jwtAccessDeniedHandler))
                .authenticationProvider(authenticationProvider())
                .authenticationManager(authenticationManager());
        return http.build();
    }
}
