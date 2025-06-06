package com.formation.events.security;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer") && bearer.length() > 7) {
      String token = bearer.substring(7);

      System.out.println(token);

      filterChain.doFilter(request, response);
    }

  }

}
