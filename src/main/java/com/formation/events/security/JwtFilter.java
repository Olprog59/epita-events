package com.formation.events.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.events.security.jwt.JwtProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final ObjectMapper objectMapper;

  @Value("${app.cookie.name:jwt_token}")
  private String cookieName;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {

      String token = extractJWT(request);

      if (token != null && !jwtProvider.isExpirated(token)) {
        String email = jwtProvider.getUserByEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails user = customUserDetailsService.loadUserByUsername(email);

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
              user.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }

      }
      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT expiré");
    } catch (SecurityException ex) {
      handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT signature invalide");
    } catch (MalformedJwtException ex) {
      handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token invalide");
    } catch (UnsupportedJwtException ex) {
      handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token non supporte");
    } catch (IllegalArgumentException ex) {
      handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT payload vide");
    } catch (Exception e) {
      handleAuthenticationError(response, HttpServletResponse.SC_BAD_REQUEST, "JWT invalide");
    }

  }

  // Methode qui extrait le token soit du Header soit du Cookie
  private String extractJWT(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer") && bearer.length() > 7) {
      // Bearer faljfasdfaksf4544; => token = "faljfasdfaksf4544";
      // Bearer &nbsp;&nbsp;&nbsp;&nbsp; ; => token = "";
      String token = bearer.substring(7).trim();
      return token.isEmpty() ? null : token;
    }

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          String token = cookie.getValue();
          if (token != null && !token.trim().isEmpty()) {
            return token;
          }
        }
      }
    }

    return null;
  }

  private void handleAuthenticationError(HttpServletResponse response, int status, String message)
      throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> errorResponse = Map.of(
        "status", status,
        "error_message", message,
        "timestamp", System.currentTimeMillis());

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

}
