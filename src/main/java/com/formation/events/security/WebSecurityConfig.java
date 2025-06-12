package com.formation.events.security;

import java.util.Arrays;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

  private final JwtFilter jwtFilter;
  private final ObjectMapper objectMapper;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated());

    http.exceptionHandling(exception -> exception
        .authenticationEntryPoint((request, response, authException) -> {
          response.setContentType("application/json;charset=UTF-8");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          Map<String, Object> errors = Map.of(
              "status", HttpServletResponse.SC_UNAUTHORIZED,
              "message", "Non autorisé");
          response.getWriter().write(objectMapper.writeValueAsString(errors));
        })
        .accessDeniedHandler((request, response, authException) -> {
          response.setContentType("application/json;charset=UTF-8");
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          Map<String, Object> errors = Map.of(
              "status", HttpServletResponse.SC_FORBIDDEN,
              "message", "Accès interdit");
          response.getWriter().write(objectMapper.writeValueAsString(errors));
        }));

    // Configuration pour H2 Console
    http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Permettre les origines Angular (dev et prod)
    configuration.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:4200", // Angular dev
        "http://localhost:3000", // Alternative dev port
        "https://localhost:4200" // HTTPS dev
    ));

    // Méthodes HTTP autorisées
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    // Headers autorisés
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"));

    // Headers exposés au client
    configuration.setExposedHeaders(Arrays.asList(
        "Authorization",
        "Set-Cookie"));

    // Permettre les cookies et credentials
    configuration.setAllowCredentials(true);

    // Durée de cache pour les preflight requests
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
